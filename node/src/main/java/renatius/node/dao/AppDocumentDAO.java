package renatius.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import renatius.node.entity.AppDocument;

@Repository
public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
