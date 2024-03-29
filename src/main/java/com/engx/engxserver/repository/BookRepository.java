package com.engx.engxserver.repository;

import com.engx.engxserver.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "select * from books where owner_id = ?1", nativeQuery = true)
    List<Book> findAllByOwner(Long ownerId);

    @Query(value = "SELECT * FROM books WHERE LOWER(name) LIKE LOWER(CONCAT('%', ?1, '%'))", nativeQuery = true)
    List<Book> findBookWithNameContain(String name);

    @Query(value = "SELECT b FROM Book b INNER JOIN User u ON b.owner.id = u.id WHERE u.userRole = 'ADMIN'")
    List<Book> findAllAdminBooks();

    @Transactional
    @Modifying
    @Query(
            value = "insert into books (id, name, owner_id, photo_url, description) values (?1, ?2, ?3, ?4, ?5)",
            nativeQuery = true)
    void insertCSV(Long id, String name, Long ownerId, String photoUrl, String description);
}
