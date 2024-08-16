package qcp.orm;

import java.util.Properties;

import qcp.orm.adapters.Group2UserDao;
import qcp.orm.adapters.GroupDao;
import qcp.orm.adapters.Library2GroupDao;
import qcp.orm.adapters.Library2SchemeDao;
import qcp.orm.adapters.LibraryDao;
import qcp.orm.adapters.Project2GroupDao;
import qcp.orm.adapters.ProjectDao;
import qcp.orm.adapters.QuantumMatrixDao;
import qcp.orm.adapters.QuantumSchemeDao;
import qcp.orm.adapters.UserDao;
import qcp.orm.entities.Group;
import qcp.orm.entities.QuantumMatrix;
import qcp.orm.entities.QuantumScheme;

public class QCPSession extends HibernateSession {
	private final Group2UserDao		g2uDao;
	private final GroupDao			gDao;
	private final Library2GroupDao	l2gDao;
	private final Library2SchemeDao	l2sDao;
	private final LibraryDao		lDao;
	private final Project2GroupDao	p2gDao;
	private final ProjectDao		pDao;
	private final QuantumMatrixDao	qmDao;
	private final QuantumSchemeDao	qsDao;
	private final UserDao			uDao;
	
	public QCPSession(final Properties props) {
		super(props);
		this.g2uDao = new Group2UserDao(this);
		this.gDao = new GroupDao(this);
		this.l2gDao = new Library2GroupDao(this);
		this.l2sDao = new Library2SchemeDao(this);
		this.lDao = new LibraryDao(this);
		this.p2gDao = new Project2GroupDao(this);
		this.pDao = new ProjectDao(this);
		this.qmDao = new QuantumMatrixDao(this);
		this.qsDao = new QuantumSchemeDao(this);
		this.uDao = new UserDao(this);
	}

	public Group2UserDao getGroup2UserDao() {
		return g2uDao;
	}
	
	public GroupDao getGroupDao() {
		return gDao;
	}
	
	public Library2GroupDao getLibrary2GroupDao() {
		return l2gDao;
	}
	
	public Library2SchemeDao getLibrary2SchemeDao() {
		return l2sDao;
	}
	
	public LibraryDao getLibraryDao() {
		return lDao;
	}
	
	public Project2GroupDao getProject2GroupDao() {
		return p2gDao;
	}
	
	public ProjectDao getProjectDao() {
		return pDao;
	}
	
	public QuantumMatrixDao getQuantumMatrixDao() {
		return qmDao;
	}
	
	public QuantumSchemeDao getQuantumSchemeDao() {
		return qsDao;
	}
	
	public UserDao getUserDao() {
		return uDao;
	}
}
