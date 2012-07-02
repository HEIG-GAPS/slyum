package dataRecord;

import graphic.GraphicComponent;
import graphic.relations.DependencyView;
import graphic.relations.InheritanceView;
import graphic.relations.RelationView;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import swing.PanelClassDiagram;
import classDiagram.IDiagramComponent;
import classDiagram.components.Entity;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;

public class Layout
{
	private final classDiagram.ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
	private LinkedList<HashSet<Entity>> pater = new LinkedList<HashSet<Entity>>();
	// liste with all the Dependecy relationships (first the parent then all the children)
	private LinkedList<LinkedList<Entity>> implSources = new LinkedList<LinkedList<Entity>>();
	LinkedList<LinkedList<Entity>>	mListe = new LinkedList<LinkedList<Entity>>();
	private final int distanceH = 250;
	private final int ScreenWidth =  Toolkit.getDefaultToolkit().getScreenSize().width-300;
	//private final int wHeight = PanelClassDiagram.getInstance().getCurrentGraphicView().getGraphicView().getScene().getHeight();
	
	public void layout()
	{
		PanelClassDiagram.getInstance().getCurrentGraphicView().setStopRepaint(true);
		
		initListe();
		
		getHierarchy();
		
		fixDependency();
		
		addEntityWithRoles();
		
		addOtherComponent();
		
		improveTreePosition();
			
		//draw
		int dy = 50;
		int globalDx = 0;
		int setSize=0;
		for (LinkedList<Entity> set : mListe)
		{
			globalDx = 0;
			int maxHeight = 0;
			int w = 0;
			int oldW = 0;
			int index = 1;
			for (Entity entity : set)
			{
				setSize = set.size()+1;
				globalDx = (ScreenWidth / setSize)*index;
				oldW = w;
				w = getWidth(entity);
				while(globalDx < oldW)
				{
					globalDx+=50;
				}
				moveTo(entity, globalDx, dy);
				int tmp = getHeight(entity);
				if(maxHeight < tmp)
					maxHeight = tmp;
				index++;
			}
			dy+= maxHeight;
			dy+= distanceH;
		}
		
		
		if(getMaxLvl() > 2)
			PanelClassDiagram.getInstance().getCurrentGraphicView().adaptDiagramToWindow();
		
		fixRelationLines();
		
		PanelClassDiagram.getInstance().getCurrentGraphicView().goRepaint();
		
		System.out.println("OUT");
	}
	
	
	private void getHierarchy()
	{
		int exit = 0;	
		int nroPasse = 0;
		// minumum 2 times
		while (exit != 0 || nroPasse <= 1)
		{
			exit = 0;
			nroPasse++;
			for (IDiagramComponent component : classDiagram.getComponents())
			{
				if(component.getClass() == Dependency.class)
				{
					Dependency dep = (Dependency)component;
					if (nroPasse == 1)
					{
						pater.get(0).add(dep.getTarget());
						pater.get(1).add(dep.getSource());
						
						boolean addListe = true;
						for(int l=0; l < implSources.size(); l++ )
						{
							if(implSources.get(l).contains(dep.getTarget()))
							{
								implSources.get(l).addLast(dep.getSource());
								addListe = false;
							}
						}
						if(addListe || implSources.isEmpty())
						{
							LinkedList<Entity> newList = new LinkedList<Entity>();
							newList.add(dep.getTarget());
							newList.add(dep.getSource());
							implSources.add(newList);
						}		
					}
					else
					{
						int parentLvl = getLvl(dep.getTarget());
						int childLvl = getLvl(dep.getSource());
						if(childLvl <= parentLvl)
						{
							pater.get(childLvl).remove(dep.getSource());
							pater.get(parentLvl+1).add(dep.getSource());
							exit++;
						}
					}
				}
				
				if(component.getClass() == Inheritance.class)
				{
					Inheritance inh = (Inheritance)component;
					if (nroPasse == 1)
					{
						pater.get(0).add(inh.getParent());
						pater.get(1).add(inh.getChild());		
					}
					else
					{
						int parentLvl = getLvl(inh.getParent());
						int childLvl = getLvl(inh.getChild());
						if(childLvl <= parentLvl)
						{
							pater.get(childLvl).remove(inh.getChild());
							pater.get(parentLvl+1).add(inh.getChild());
							exit++;
						}
					}
				}
			}	
		}
	}
	
	private void improveTreePosition()
	{
		for (HashSet<Entity> linkedList : pater)
		{
			Iterator<Entity> it = linkedList.iterator();
			LinkedList<Entity> tmp = new LinkedList<Entity>();
			mListe.add(tmp);
			while(it.hasNext())
			{
				tmp.add(it.next());
			}
		}
		
		int lvl = 0;
		int nChilds = 0;
		for (LinkedList<Entity> linkedList : mListe)
		{
			if(lvl <= getMaxLvl())
				for (Entity parent : linkedList)
				{
					for(int i=0; i<mListe.get(lvl+1).size(); i++)
					{
						if(nChilds<mListe.get(lvl+1).size())
						{
							if(isInterfaceChild(parent, mListe.get(lvl+1).get(i)))
							{
								mListe.get(lvl+1).addLast(mListe.get(lvl+1).remove(i));
							}
						}
						
					}
				}
			
			nChilds = 0;
			if(lvl <= getMaxLvl())
				for (Entity parent : linkedList)
				{
					for(int i=0; i<mListe.get(lvl+1).size(); i++)
					{
						if(nChilds<mListe.get(lvl+1).size())
						{
							if(mListe.get(lvl+1).get(i).isChildOf(parent))
							{
								mListe.get(lvl+1).add(nChilds, mListe.get(lvl+1).remove(i));
								nChilds++;
							}
						}
						
					}
				}
			lvl++;
		}
	}
	
	private void fixDependency()
	{
		// set interface one level up of the highest child 
		for (LinkedList<Entity> liste: implSources)
		{
			int maxLvl = 20;
			for (int i = 1; i<liste.size() ;i++)
			{
				int lvl = getLvl(liste.get(i));
				if (lvl<maxLvl)
					maxLvl = lvl;
			}
			
			int parentlvl = getLvl(liste.get(0));
			if (parentlvl != maxLvl-1);
			{
				pater.get(parentlvl).remove(liste.get(0));
				pater.get(maxLvl-1).add(liste.get(0));
			}
		}
	}
	
	
	private void moveTo(IDiagramComponent component, int dx, int dy)
	{
		for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
		{
			try
			{
				if(gc.getAssociedComponent().equals(component))
				{
					gc.setBounds(new Rectangle(dx,dy, gc.getBounds().width, gc.getBounds().height));
				}
				
			} 
			catch (Exception e){
				// has no associated Component
			}
		}
	}
	
	private void fixRelationLines()
	{
		// correct lineView
		for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
		{
			if(gc.getClass() == InheritanceView.class || gc.getClass() == DependencyView.class)
			{
				RelationView dv = (RelationView)gc;
				
				//fix target point
				int posX = (int) dv.getLastPoint().getAnchor().getX();
				int posY = dv.getLastPoint().getAnchor().y;
				int newX = (int) dv.getGraphicView().getComponentAtPosition(new Point(posX, posY-40)).getBounds().getCenterX();
				int newY = (int) dv.getGraphicView().getComponentAtPosition(new Point(posX, posY-40)).getBounds().getMaxY();
				//System.out.println(dv.getGraphicView().getComponentAtPosition(new Point(posX, posY-40)) + " x "+posX+" y "+posY);
	
				dv.getLastPoint().setAnchor(new Point(newX, newY));
				
				
				//fix source point
				int pos2X = dv.getFirstPoint().getAnchor().x;
				int pos2Y = dv.getFirstPoint().getAnchor().y;
				int new2X = (int) dv.getGraphicView().getComponentAtPosition(new Point(pos2X, pos2Y+20)).getBounds().getCenterX();
				int new2Y = (int) dv.getGraphicView().getComponentAtPosition(new Point(pos2X, pos2Y+20)).getBounds().getMinY();
				
//				System.out.println("T: "+newX+" "+newY);
//				System.out.println("S: "+new2X+" "+new2Y);
//				
//				System.out.println("--------");
				dv.getFirstPoint().setAnchor(new Point(new2X, new2Y));
	
			}
		}
	}
	
	private void addEntityWithRoles()
	{	
		boolean newlyOne = true;
		boolean newlyTwo = true;
		int max = getMaxLvl();
		for (IDiagramComponent component : classDiagram.getComponents())
		{
			if(component.getClass() == Binary.class)
			{
				Binary b = (Binary) component;
				Entity first = b.getRoles().getFirst().getEntity();
				Entity last = b.getRoles().getLast().getEntity();
				for (HashSet<Entity> list : pater)
				{
					if(list.contains(first))
						newlyOne = false;
					if(list.contains(last))
						newlyTwo = false;
				}
				if(newlyOne)
					pater.get(max).add(first);
				if(newlyTwo)
					pater.get(max).add(last);
			}
		}
		
	}


	private void addOtherComponent()
	{
		int max = getMaxLvl();
		for (IDiagramComponent component : classDiagram.getComponents())
		{
			boolean newlyOne = true;
			if(component instanceof Entity)
			{
				Entity e = (Entity)component;
				for (HashSet<Entity> list : pater)
				{
					if(list.contains(e))
						newlyOne = false;
				}
				if(newlyOne)
					pater.get(max).add(e);
			}
		}
	}
	
	private int getLvl(Entity e)
	{
		for (int index=0; index<pater.size(); index++)
		{
			if(pater.get(index).contains(e))
				return index;
		}
		return -1;
	}
	
	private int getMaxLvl()
	{
		int maxLvl = 0;
		for (int list=0; list<pater.size(); list++)
		{
			if(pater.get(list).isEmpty())
			{
				maxLvl = list;
				break;
			}
		}
		return maxLvl;
	}
	
	private int getWidth(IDiagramComponent component)
	{
		for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
		{
			try
			{
				if(gc.getAssociedComponent().equals(component))
				{
					//System.out.println("return x: " + gc.getBounds().getMaxX() + " \tw: "+gc.getBounds().width);
					return ((int)gc.getBounds().getCenterX() + gc.getBounds().width);
				}
			} 
			catch (Exception e){
				// has no associated Component
			}
		}
		return 0;
	}
	
	private int getHeight(IDiagramComponent component)
	{
		for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
		{
			try
			{
				if(gc.getAssociedComponent().equals(component))
				{
					return (int)gc.getBounds().height;
				}
			} 
			catch (Exception e){
				// has no associated Component
			}
		}
		return 0;
	}
	
	private void initListe()
	{
		for (int i = 0; i < 9; i++)
		{
			pater.add(new HashSet<Entity>());
		}
	}
	
//	private int getMaxGlobalX()
//	{
//		int max = 0;	
//		for (LinkedList<Entity> list : mListe)
//		{
//			int i = list.isEmpty()?0:getWidth(list.getLast());
//			if(i > max)
//				max = i;
//		}
//		
//		System.out.println("Max: " + max);
//		return max;
//	}
	
	
	private boolean isInterfaceChild(Entity target, Entity source)
	{
		for (LinkedList<Entity> implListe : implSources)
		{
			if(implListe.contains(target) && implListe.contains(source))
			{
				return true;
			}
		}
		return false;
	}
}


//
//if(component instanceof Entity)
//{
//	Entity entity = (Entity)component;
//	System.out.println(entity.getName() );
//	for (Entity e : entity.getAllChilds())
//	{
//		System.out.println("\tchilds >"+e.getName());
//	}
//	for (Entity e : entity.getAllParents())
//	{
//		System.out.println("\tparents <"+e.getName());
//	}
//	System.out.println("------");
//}
//	
//	for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
//	{
//		System.out.println(gc.getClass());
//		if(gc.getClass() == InterfaceView.class)
//		{
//			currentH = gc.getBounds().height;
//			currentW = gc.getBounds().width;
//			moveTo(gc, dx, dy);
//			dx += currentW + 50;
//			haveInterface = true;
//		}
//	}
//	
//	if(haveInterface)
//		dy += currentH + 100;
//	
//	for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
//	{
//		if(gc.getClass() == ClassView.class)
//		{
//			currentH = gc.getBounds().height;
//			currentW = gc.getBounds().width;
//			//System.out.println("dxy "+dx+ " " + dy);
//			ClassView cv = (ClassView) gc;
//			cv.setBounds(new Rectangle(dx,dy,currentW,currentH));
//			//System.out.println("XY: " + gc.getBounds().getX() + " - "+ gc.getBounds().getY());
//			//System.out.println("height-weight: " + gc.getBounds().getHeight() + " - "+ gc.getBounds().getWidth());
//			//System.out.println("-----------");
//			
//			dx += currentW+50;
//		}
//		//PanelClassDiagram.getInstance().getCurrentGraphicView().goRepaint();
//		
//		
//	}
//	//PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents().getFirst().getBounds().getLocation().translate(20, 20);
//}
