package car.ccut.com.vehicle.bean.address;

import java.io.Serializable;
import java.util.ArrayList;

public class Province implements Serializable{
	String areaId;
	String areaName;
	ArrayList<City> cities = new ArrayList<City>();


	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public ArrayList<City> getCities() {
		return cities;
	}
	public void setCities(ArrayList<City> cities) {
		this.cities = cities;
	}
	
}
