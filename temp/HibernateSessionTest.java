package qcp.orm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import qcp.api.interfaces.UsersInterface;

public class HibernateSessionTest {
	@Test
	public void qcpSessionTest() throws IOException {
		final Properties	props = new Properties();
		
		try(final InputStream	is = getClass().getResourceAsStream("/hibernate.cfg")) {
			props.load(is);
		}

		try(final QCPSession	session = new QCPSession(props)) {
			Assert.assertFalse(session.getUserDao().findAll().isEmpty());
			Assert.assertNotNull(session.getUserDao().findById(UsersInterface.ADMIN_USER_ID));

			Assert.assertFalse(session.getGroupDao().findAll().isEmpty());
			Assert.assertNotNull(session.getGroupDao().findById(UsersInterface.SYSTEM_GROUP_ID));
		}
	}

	@Test
	public void qcpScenarioTest() throws IOException {
		final Properties	props = new Properties();
		
		try(final InputStream	is = getClass().getResourceAsStream("/hibernate.cfg")) {
			props.load(is);
		}

		try(final QCPSession	session = new QCPSession(props)) {
			
		}
	}
}
