package com.deepaksir.service;

import com.deepaksir.entity.Bookmark;
import com.deepaksir.entity.User;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.BookmarkRepository;
import com.deepaksir.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Bookmark> getUserBookmarks() {
        UUID userId = getCurrentUserId();
        return bookmarkRepository.findByUserId(userId);
    }

    @Transactional
    public Bookmark addBookmark(String type, UUID itemId) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setType(type);
        bookmark.setItemId(itemId);

        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(UUID id) {
        bookmarkRepository.deleteById(id);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            throw new ApiException("Not authenticated");
        }

        // Principal User entity hai to usse ID lo
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return ((User) principal).getId();
        }

        // Principal String hai (email) to email se user dhundho
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found: " + email));
        return user.getId();
    }
}