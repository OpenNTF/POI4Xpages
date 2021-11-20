/**
 * Copyright (c) 2012-2021 WebGate Consulting AG and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public static Contact buildContact(String firstName, String lastName, String email, String city, String state) {
		Contact contact = new Contact();
		contact.setFirstName(firstName);
		contact.setLastName(lastName);
		contact.setCity(city);
		contact.setEMail(email);
		contact.setState(state);
		return contact;
	}
}
