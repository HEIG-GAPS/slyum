package dataRecord.io;

import java.io.File;
import java.util.LinkedList;

import dataRecord.elements.CompilationUnit;

public interface Parser
{
	public LinkedList<CompilationUnit> parse(File[] files);
}
