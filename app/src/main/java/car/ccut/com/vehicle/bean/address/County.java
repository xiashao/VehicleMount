package car.ccut.com.vehicle.bean.address;

import java.io.Serializable;

public class County implements Serializable{
	private String areaId;
	private String areaName;
	
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
}
