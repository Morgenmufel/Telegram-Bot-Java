package renatius.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import renatius.node.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
