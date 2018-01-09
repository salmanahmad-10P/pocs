import java.util.*;

public class Person {
	private int Id = 0;
	private String name;
	private Set friends;
	private Double cashOnHand;
	private Address[] addresses;

	public int getId()
	{	return Id;	}
	public void setId(int x)
	{	Id = x;	}
	public String getName()
	{	return name;	}
	public void setName(String x)
	{	name = x;	}
	public Set getFriends()
	{	return friends;	}
	public void setFriends(Set x)
	{	friends = x;	}
	public Double getCashOnHand()
	{	return cashOnHand;	}
	public void setCashOnHand(Double x)
	{	cashOnHand = x;	}
	public Address[] getAddresses()
	{	return addresses;	}
	public void setAddresses(Address[] x)
	{	addresses = x;	}
}

class Address {

	private int addressId = 0;

	public int getAddressId()
	{	return addressId;	}
	public void setAddressId(int x)
	{	addressId = x;	}
}
