package biz.webgate.domino.poi.demodb.contact;

import java.io.Serializable;

public class Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String m_FirstName;
	private String m_LastName;
	private String m_City;
	private String m_State;
	private String m_EMail;

	public void setLastName(String lastName) {
		m_LastName = lastName;
	}

	public String getLastName() {
		return m_LastName;
	}

	public String getFirstName() {
		return m_FirstName;
	}

	public void setFirstName(String firstName) {
		m_FirstName = firstName;
	}

	public String getCity() {
		return m_City;
	}

	public void setCity(String city) {
		m_City = city;
	}

	public String getState() {
		return m_State;
	}

	public void setState(String state) {
		m_State = state;
	}

	public String getEMail() {
		return m_EMail;
	}

	public void setEMail(String mail) {
		m_EMail = mail;
	}
}
