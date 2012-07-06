package dataRecord.io;

import graphic.GraphicComponent;
import graphic.relations.DependencyView;
import graphic.relations.InheritanceView;

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
	private final int DISTANCE_H = 200;
	private final int ScreenWidth =  Toolkit.getDefaultToolkit().getScreenSize().width-200;
	private double currentWidth = ScreenWidth;
	private final int INCREASE_DX = 35;
	
	public void layout()
	{
		PanelClassDiagram.getInstance().getCurrentGraphicView().setStopRepaint(true);
		
		initListe();
		
		getHierarchy();
		
		fixDependency();
		
		addEntityWithRoles();
		
		addOtherComponent();
		
		improveTreePosition();
		
		if(getMaxLvl() > 2)
			currentWidth = ScreenWidth*2;
		
		draw();
		
		if(getMaxLvl() > 2)
			PanelClassDiagram.getInstance().getCurrentGraphicView().adaptDiagramToWindow();
		
		fixRelationLines();
		fixRelationLines();
		
		PanelClassDiagram.getInstance().getCurrentGraphicView().goRepaint();
		
		System.out.println("-END-");
	}
	
	private void draw()
	{
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
				globalDx = (int) ((currentWidth / setSize)*index);
				oldW = w;
				w = getWidth(entity);
				while(globalDx < oldW)
				{
					globalDx+=INCREASE_DX;
				}
				moveTo(entity, globalDx, dy);
				int tmp = getHeight(entity);
				if(maxHeight < tmp)
					maxHeight = tmp;
				index++;
			}
			dy+= maxHeight;
			dy+= DISTANCE_H;
		}
	}
	
	/**
	 * set the elements in order from the most top-level to the lowest level 
	 * Top level are interfaces and superClasses and low level are top level childs
	 */
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
	
	/**
	 * remove line crossing for the inheritance and dependency relationship
	 */
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
							if(isInterfaceChild(parent, mListe.get(lvl+1).get(i)))
							{
								mListe.get(lvl+1).add(nChilds,mListe.get(lvl+1).remove(i));
								nChilds++;
							}
						}
					}
				}
			lvl++;
		}
	}
	
	/**
	 * set interface one level up of the highest child 
	 */
	private void fixDependency()
	{
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
	
	/**
	 * move the element from a point A(x, y) to a point B(dx, dy)
	 * 
	 * @param the component to move
	 * @param dx the new coordinate x
	 * @param dy the new coordinate y
	 */
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
	
	/**
	 * Start the inheritance or the dependency line form the middle top of the child to the middle bottom 
	 * of the parent.
	 * For a better lisibility of the diagram the relationship lines are made with square angles
	 */
	void fixRelationLines()
	{
		// correct lineView
		for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
		{
			if(gc.getClass() == InheritanceView.class )
			{
				InheritanceView dv = (InheritanceView)gc;
				Inheritance assoc =  (Inheritance) dv.getAssociedComponent();
				
				//fix target point
				Entity parent = assoc.getParent();
				GraphicComponent graphic = getView(parent);

				int newX = (int) graphic.getBounds().getCenterX();
				int newY = (int) graphic.getBounds().getMaxY();
				
				dv.getLastPoint().setAnchor(new Point(newX, newY));
				//fix source point
				int pos2X = dv.getFirstPoint().getAnchor().x;
				int pos2Y = dv.getFirstPoint().getAnchor().y;
				int new2X = (int) dv.getGraphicView().getComponentAtPosition(new Point(pos2X, pos2Y+20)).getBounds().getCenterX();
				int new2Y = (int) dv.getGraphicView().getComponentAtPosition(new Point(pos2X, pos2Y+20)).getBounds().getMinY();
				
				dv.getFirstPoint().setAnchor(new Point(new2X, new2Y));
				
				if(newX != new2X)
				{
					Point middleBound = dv.middleBounds();
					if( dv.getPoints().size()<4)
					{
						dv.createNewGrip(middleBound);
						dv.createNewGrip(middleBound);
					}
					dv.getPoints().get(1).setAnchor(new Point(new2X,middleBound.y));
					dv.getPoints().get(2).setAnchor(new Point(newX,middleBound.y));
				}
			}
			
			if(gc.getClass() == DependencyView.class )
			{
				DependencyView dv = (DependencyView)gc;
				Dependency assoc =  (Dependency) dv.getAssociedComponent();
				
				//fix target point
				Entity parent2 = assoc.getTarget();
				int newX = (int) getView(parent2).getBounds().getCenterX();
				int newY = (int) getView(parent2).getBounds().getMaxY();
				
				dv.getLastPoint().setAnchor(new Point(newX, newY));
				
				//fix source point
				int pos2X = dv.getFirstPoint().getAnchor().x;
				int pos2Y = dv.getFirstPoint().getAnchor().y;
				int new2X = (int) dv.getGraphicView().getComponentAtPosition(new Point(pos2X, pos2Y+20)).getBounds().getCenterX();
				int new2Y = (int) dv.getGraphicView().getComponentAtPosition(new Point(pos2X, pos2Y+20)).getBounds().getMinY();
				
				dv.getFirstPoint().setAnchor(new Point(new2X, new2Y));
				
				if(newX != new2X)
				{
					Point middleBound = dv.middleBounds();
					if( dv.getPoints().size()<4)
					{
						dv.createNewGrip(middleBound);
						dv.createNewGrip(middleBound);
					}
					dv.getPoints().get(1).setAnchor(new Point(new2X,middleBound.y-20));
					dv.getPoints().get(2).setAnchor(new Point(newX,middleBound.y-20));
				}
			}
	
		}
	}
	
	/**
	 * add the entities with a association line
	 */
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

	/**
	 * add the entities that havent been added because they have no relation ship lines
	 */
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
	
	/**
	 * get the element level inside the tree
	 * 
	 * @param e
	 * @return the level
	 */
	private int getLvl(Entity e)
	{
		for (int index=0; index<pater.size(); index++)
		{
			if(pater.get(index).contains(e))
				return index;
		}
		return -1;
	}
	
	/**
	 * return the deepest level of the tree
	 * 
	 * @return the deepest level
	 */
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
	
	/**
	 * return the width of the element
	 * 
	 * @param component
	 * @return the width
	 */
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
	
	/**
	 * return the height of the element
	 * 
	 * @param component
	 * @return the height
	 */
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
	
	private GraphicComponent getView (IDiagramComponent component)
	{
		for (GraphicComponent gc : PanelClassDiagram.getInstance().getCurrentGraphicView().getAllComponents())
		{
			try
			{
				if(gc.getAssociedComponent().equals(component))
				{
					return gc;
				}
			} 
			catch (Exception e){
				// has no associated Component
			}
		}
		return null;
	}
	
	private void initListe()
	{
		for (int i = 0; i < 9; i++)
		{
			pater.add(new HashSet<Entity>());
		}
	}
	
	/**
	 * return true if the source is implementing the interface target, false otherwise
	 * 
	 * @param target
	 * @param source
	 * @return a boolean
	 */
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

