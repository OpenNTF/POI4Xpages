package biz.webgate.domino.poi.demodb.contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lotus.domino.Document;
import lotus.domino.View;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class ContactBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public List<Contact> getAllContacts() {
		List<Contact> lstRC = new ArrayList<Contact>();
		try {
			View viwLUP = ExtLibUtil.getCurrentDatabase().getView("AllContacts");
			Document docNext = viwLUP.getFirstDocument();
			while (docNext != null) {
				Document docProcess = docNext;
				docNext = viwLUP.getNextDocument(docNext);

				Contact conCurrent = new Contact();
				conCurrent.setFirstName(docProcess.getItemValueString("FirstName"));
				conCurrent.setLastName(docProcess.getItemValueString("LastName"));
				conCurrent.setCity(docProcess.getItemValueString("City"));
				conCurrent.setEMail(docProcess.getItemValueString("EMail"));
				conCurrent.setState(docProcess.getItemValueString("State"));
				lstRC.add(conCurrent);
				docProcess.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstRC;
	}

	public List<Contact> get3AddressEntries() {
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(Contact.buildContact("Martin", "Meier", "martin.meier@acme.com", "Zürich", "ZH"));
		contacts.add(Contact.buildContact("Peter", "Müller", "peter.müller@acme.com", "Rorschach", "SG"));
		contacts.add(Contact.buildContact("René", "Dupont", "rené.dupont@acme.com", "Genève", "GE"));
		return contacts;
	}

}
