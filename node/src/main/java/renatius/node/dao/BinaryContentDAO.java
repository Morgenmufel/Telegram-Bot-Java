package renatius.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import renatius.node.entity.BinaryContent;

@Repository
public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
