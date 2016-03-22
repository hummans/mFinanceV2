package com.nfortics.mfinanceV2.logic;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.com.shptbm.DecodeWlt;

public class ParseSFZAPI {
	private static final byte[] command = "D&C00040101".getBytes();

	private static final String SUCCESS = "AAAAAA96690508000090";

	public static final int DATA_SIZE = 1295;

	private byte[] buffer = new byte[DATA_SIZE];

	//private BluetoothChatService chatService;
	private CommunicateManager communicateManager;

	private String path = Environment.getExternalStorageDirectory()
			+ File.separator + "wltlib";

	public ParseSFZAPI(CommunicateManager communicateManager) {
		this.communicateManager = communicateManager;
	}

	private Result result;

	/**
	 * ��ȡ���֤��Ϣ���˷���Ϊ�����ģ�����������̴߳���
	 */
	public Result read() {
		byte[] commands = new byte[7 + command.length];
		commands[0] = 2;
		commands[1] = 0;
		commands[2] = 0;
		commands[3] = 0;
		commands[4] = 1;
		commands[5] = 0;
		commands[6] = (byte) command.length;
		for (int i = 0; i < command.length; i++) {
			commands[7 + i] = command[i];
		}
		result = new Result();
		communicateManager.write(commands);

		int length = communicateManager.read(buffer, 3000, 300,DATA_SIZE);
		if (length == 0) {
			result.confirmationCode = Result.TIME_OUT;
			return result;
		}

		People people = decode(buffer);
		if (people == null) {
			result.confirmationCode = Result.FIND_FAIL;
		} else {
			result.confirmationCode = Result.SUCCESS;
			result.resultInfo = people;
		}
		return result;
	}

	private People decode(byte[] buffer) {
		if (buffer == null) {
			return null;
		}
		byte[] b = new byte[10];
		System.arraycopy(buffer, 0, b, 0, 10);
		String result = toHexString(b);
		Log.i("whw", "prefix result=" + result);
		People people = null;
		if (result.equalsIgnoreCase(SUCCESS)) {
			byte[] data = new byte[buffer.length - 10];
			System.arraycopy(buffer, 10, data, 0, buffer.length - 10);
			people = decodeInfo(data);
		}
		return people;

	}

	private People decodeInfo(byte[] buffer) {
		short textSize = getShort(buffer[0], buffer[1]);
		short imageSize = getShort(buffer[2], buffer[3]);
		byte[] text = new byte[textSize];
		System.arraycopy(buffer, 4, text, 0, textSize);
		byte[] image = new byte[imageSize];
		System.arraycopy(buffer, 4 + textSize, image, 0, imageSize);
		People people = null;
		try {
			String temp = null;
			people = new People();
			// ����
			temp = new String(text, 0, 30, "UTF-16LE").trim();
			people.setPeopleName(temp);

			// �Ա�
			temp = new String(text, 30, 2, "UTF-16LE");
			if (temp.equals("1"))
				temp = "��";
			else
				temp = "Ů";
			people.setPeopleSex(temp);

			// ����
			temp = new String(text, 32, 4, "UTF-16LE");
			try {
				int code = Integer.parseInt(temp.toString());
				temp = decodeNation(code);
			} catch (Exception e) {
				temp = "";
			}
			people.setPeopleNation(temp);

			// ����
			temp = new String(text, 36, 16, "UTF-16LE").trim();
			people.setPeopleBirthday(temp);

			// סַ
			temp = new String(text, 52, 70, "UTF-16LE").trim();
			people.setPeopleAddress(temp);

			// ���֤��
			temp = new String(text, 122, 36, "UTF-16LE").trim();
			people.setPeopleIDCode(temp);

			// ǩ������
			temp = new String(text, 158, 30, "UTF-16LE").trim();
			people.setDepartment(temp);

			// ��Ч��ʼ����
			temp = new String(text, 188, 16, "UTF-16LE").trim();
			people.setStartDate(temp);

			// ��Ч��ֹ����
			temp = new String(text, 204, 16, "UTF-16LE").trim();
			people.setEndDate(temp);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return null;
		}

		people.setPhoto(parsePhoto(image));
		return people;
	}

	private String decodeNation(int code) {
		String nation;
		switch (code) {
		case 1:
			nation = "��";
			break;
		case 2:
			nation = "�ɹ�";
			break;
		case 3:
			nation = "��";
			break;
		case 4:
			nation = "��";
			break;
		case 5:
			nation = "ά���";
			break;
		case 6:
			nation = "��";
			break;
		case 7:
			nation = "��";
			break;
		case 8:
			nation = "׳";
			break;
		case 9:
			nation = "����";
			break;
		case 10:
			nation = "����";
			break;
		case 11:
			nation = "��";
			break;
		case 12:
			nation = "��";
			break;
		case 13:
			nation = "��";
			break;
		case 14:
			nation = "��";
			break;
		case 15:
			nation = "����";
			break;
		case 16:
			nation = "����";
			break;
		case 17:
			nation = "������";
			break;
		case 18:
			nation = "��";
			break;
		case 19:
			nation = "��";
			break;
		case 20:
			nation = "����";
			break;
		case 21:
			nation = "��";
			break;
		case 22:
			nation = "�";
			break;
		case 23:
			nation = "��ɽ";
			break;
		case 24:
			nation = "����";
			break;
		case 25:
			nation = "ˮ";
			break;
		case 26:
			nation = "����";
			break;
		case 27:
			nation = "����";
			break;
		case 28:
			nation = "����";
			break;
		case 29:
			nation = "�¶�����";
			break;
		case 30:
			nation = "��";
			break;
		case 31:
			nation = "���Ӷ�";
			break;
		case 32:
			nation = "����";
			break;
		case 33:
			nation = "Ǽ";
			break;
		case 34:
			nation = "����";
			break;
		case 35:
			nation = "����";
			break;
		case 36:
			nation = "ë��";
			break;
		case 37:
			nation = "����";
			break;
		case 38:
			nation = "����";
			break;
		case 39:
			nation = "����";
			break;
		case 40:
			nation = "����";
			break;
		case 41:
			nation = "������";
			break;
		case 42:
			nation = "ŭ";
			break;
		case 43:
			nation = "���α��";
			break;
		case 44:
			nation = "����˹";
			break;
		case 45:
			nation = "���¿�";
			break;
		case 46:
			nation = "�°�";
			break;
		case 47:
			nation = "����";
			break;
		case 48:
			nation = "ԣ��";
			break;
		case 49:
			nation = "��";
			break;
		case 50:
			nation = "������";
			break;
		case 51:
			nation = "����";
			break;
		case 52:
			nation = "���״�";
			break;
		case 53:
			nation = "����";
			break;
		case 54:
			nation = "�Ű�";
			break;
		case 55:
			nation = "���";
			break;
		case 56:
			nation = "��ŵ";
			break;
		case 97:
			nation = "����";
			break;
		case 98:
			nation = "���Ѫͳ�й�����ʿ";
			break;
		default:
			nation = "";
		}

		return nation;
	}

	/**
	 * ����ת��16�����ַ���
	 * 
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

	private short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}

	private byte[] parsePhoto(byte[] wltdata) {
		String bmpPath = path + File.separator + "zp.bmp";
		String wltPath = path + File.separator + "zp.wlt";
		if (!isExistsParsePath(wltPath, wltdata)) {
			return null;
		}

		int result = DecodeWlt.Wlt2Bmp(wltPath, bmpPath);
		Log.i("whw", "wltdata length=" + wltdata.length + "   result=" + result);
		if (result == 1) {
			byte[] image = getBytes(bmpPath);
			return image;
		} else {
			return null;
		}
	}

	private boolean isExistsParsePath(String wltPath, byte[] wltdata) {
		File myFile = new File(path);
		boolean isMKDir = true;
		if (!myFile.exists()) {
			isMKDir = myFile.mkdir();
		}
		if (!isMKDir) {
			return false;
		}

		File wltFile = new File(wltPath);
		boolean isCreate = true;
		if (!wltFile.exists()) {
			try {
				isCreate = wltFile.createNewFile();
			} catch (IOException e) {
				isCreate = false;
				e.printStackTrace();
			}
		}
		if (!isCreate) {
			return false;
		}

		boolean isWriteData = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(wltFile);
			fos.write(wltdata);
			isWriteData = true;
		} catch (FileNotFoundException e) {
			isWriteData = false;
			e.printStackTrace();
		} catch (IOException e) {
			isWriteData = false;
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return isWriteData;
	}

	/**
	 * ���ָ���ļ���byte����
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static class People {
		/**
		 * ����
		 */
		private String peopleName;

		/**
		 * �Ա�
		 */
		private String peopleSex;

		/**
		 * ����
		 */
		private String peopleNation;

		/**
		 * ��������
		 */
		private String peopleBirthday;

		/**
		 * סַ
		 */
		private String peopleAddress;

		/**
		 * ���֤��
		 */
		private String peopleIDCode;

		/**
		 * ǩ������
		 */
		private String department;

		/**
		 * ��Ч���ޣ���ʼ
		 */
		private String startDate;

		/**
		 * ��Ч���ޣ�����
		 */
		private String endDate;

		/**
		 * ���֤ͷ��
		 */
		private byte[] photo;

		public String getPeopleName() {
			return peopleName;
		}

		public void setPeopleName(String peopleName) {
			this.peopleName = peopleName;
		}

		public String getPeopleSex() {
			return peopleSex;
		}

		public void setPeopleSex(String peopleSex) {
			this.peopleSex = peopleSex;
		}

		public String getPeopleNation() {
			return peopleNation;
		}

		public void setPeopleNation(String peopleNation) {
			this.peopleNation = peopleNation;
		}

		public String getPeopleBirthday() {
			return peopleBirthday;
		}

		public void setPeopleBirthday(String peopleBirthday) {
			this.peopleBirthday = peopleBirthday;
		}

		public String getPeopleAddress() {
			return peopleAddress;
		}

		public void setPeopleAddress(String peopleAddress) {
			this.peopleAddress = peopleAddress;
		}

		public String getPeopleIDCode() {
			return peopleIDCode;
		}

		public void setPeopleIDCode(String peopleIDCode) {
			this.peopleIDCode = peopleIDCode;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public byte[] getPhoto() {
			return photo;
		}

		public void setPhoto(byte[] photo) {
			this.photo = photo;
		}

	}

	public static class Result {
		/**
		 * 1: �ɹ�
		 */
		public static final int SUCCESS = 1;
		/**
		 * 2��ʧ��
		 */
		public static final int FIND_FAIL = 2;
		/**
		 * 3: ��ʱ
		 */
		public static final int TIME_OUT = 3;
		/**
		 * 4�������쳣
		 */
		public static final int OTHER_EXCEPTION = 4;

		/**
		 * ȷ���� 1: �ɹ� 2��ʧ�� 3: ��ʱ 4�������쳣
		 */
		public int confirmationCode;

		/**
		 * �����:��ȷ����Ϊ1ʱ�����ж��Ƿ��н��
		 */
		public Object resultInfo;
	}
}
