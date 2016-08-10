package car.ccut.com.vehicle.service;

import android.graphics.Bitmap;

import java.io.Serializable;

public class PhoneContact implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String phone;
	private Bitmap contactPhoto;//头像

	public Bitmap getContactPhoto() {
		return contactPhoto;
	}
	public void setContactPhoto(Bitmap contactPhoto) {
		this.contactPhoto = contactPhoto;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}


}
