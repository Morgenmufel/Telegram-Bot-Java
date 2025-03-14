package renatius.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import renatius.node.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
