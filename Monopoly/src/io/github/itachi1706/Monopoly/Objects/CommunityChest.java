package io.github.itachi1706.Monopoly.Objects;

public class CommunityChest {

	private int id;
	private String name;
	
	public CommunityChest(){}
	public CommunityChest(int i, String n){
		id = i;
		name = n;
	}
	public int getId(){return id;}
	public void setId(int i){id = i;}
	public String getName(){return name;}
	public void setName(String n){name = n;}
	public String toString()
	{
		return "Id: " + id + "\tName: " + name;
	}
}
