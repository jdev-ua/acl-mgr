package ua.pp.jdev.permits.data;

public interface AccessControlListPageableDAO extends AccessControlListDAO {
	Page<AccessControlList> readPage(int page);
}
