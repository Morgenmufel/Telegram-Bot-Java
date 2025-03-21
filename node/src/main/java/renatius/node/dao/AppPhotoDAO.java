package renatius.node.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import renatius.node.entity.AppPhoto;

@Repository
public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
