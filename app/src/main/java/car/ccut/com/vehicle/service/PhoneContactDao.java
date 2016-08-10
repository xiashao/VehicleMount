package car.ccut.com.vehicle.service;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.telephony.SmsManager;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取手机联系人dao
 * @author oceangray
 *
 */
public class PhoneContactDao {
	/**获取库Phon表字段**/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
	/**联系人显示名称**/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/**电话号码**/
	private static final int PHONES_NUMBER_INDEX = 1;

	/**头像ID**/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/**联系人的ID**/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	/**得到手机通讯录联系人信息**/
	public static List<PhoneContact> getPhoneContacts(Service activity) {
		List<PhoneContact> contacts=new ArrayList<PhoneContact>();
		ContentResolver resolver = activity.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String name=phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				/*if(name.contains("爸")||name.contains("妈")||name.contains("哥")||name.contains("姐")||name.contains("公")||name.contains("婆")||name.contains("鑫"))*/
				if(name.contains("琦"))
				{
				PhoneContact contact=new PhoneContact();
				//得到手机号码

				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				//得到联系人名称
			   String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				contact.setName(contactName);
				//得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				contact.setPhone(phoneNumber);
				//得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				//得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if(photoid > 0 ) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
					contact.setContactPhoto(contactPhoto);
				}
					String so2="小主人目前处于酒后状态，请及时提醒他，不要酒后驾驶";
					SmsManager sm= SmsManager.getDefault();
					sm.sendTextMessage(phoneNumber, null, so2, null, null);
				contacts.add(contact);
				}
			}
			phoneCursor.close();
		}
		return contacts;
	}
	/**得到手机通讯录联系人信息**/
	public static List<PhoneContact> searchPhoneContacts(Activity activity,String seachContent) {
		List<PhoneContact> contacts=new ArrayList<PhoneContact>();
		ContentResolver resolver = activity.getContentResolver();
		String selection= Phone.DISPLAY_NAME+" like '%"+seachContent+"%' or "+ Phone.NUMBER +" like '%"+seachContent+"%'";
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, selection, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				PhoneContact contact=new PhoneContact();
				//得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				//得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				contact.setName(contactName);
				//得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				contact.setPhone(phoneNumber);
				//得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				//得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if(photoid > 0 ) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
					contact.setContactPhoto(contactPhoto);
				}
				contacts.add(contact);
			}
			phoneCursor.close();
		}
		return contacts;
	}
	/**得到手机SIM卡联系人人信息**/
	public  static List<PhoneContact> getSIMContacts(Activity activity) {
		List<PhoneContact> contacts=new ArrayList<PhoneContact>();
		ContentResolver resolver = activity.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				PhoneContact contact=new PhoneContact();
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//Sim卡中没有联系人头像
				contact.setName(contactName);
				contact.setPhone(phoneNumber);
				contacts.add(contact);
			}
			phoneCursor.close();
		}
		return contacts;
	}
}
