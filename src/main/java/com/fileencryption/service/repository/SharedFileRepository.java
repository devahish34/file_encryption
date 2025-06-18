package com.fileencryption.service.repository;

import com.fileencryption.service.model.SharedFile;
import com.fileencryption.service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharedFileRepository extends MongoRepository<SharedFile, String> {
    List<SharedFile> findByOwner(User owner);
    
    @Query("{ 'recipientEmails': ?0 }")
    List<SharedFile> findByRecipientEmail(String email);
    
    @Query("{ 'fileId': ?0, 'recipientEmails': ?1 }")
    Optional<SharedFile> findByFileIdAndRecipientEmailsContaining(String fileId, String email);
    
    @Query("{ 'owner': ?0, 'isExpired': false }")
    List<SharedFile> findByOwnerAndIsExpiredFalse(User owner);
}