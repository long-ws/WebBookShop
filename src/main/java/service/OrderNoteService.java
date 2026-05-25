package service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import beans.OrderNote;
import dao.OrderNoteDAO;
import repository.UserRepository;

public class OrderNoteService {

	private final OrderNoteDAO noteDAO = new OrderNoteDAO();
	private final UserRepository userRepository;

	public OrderNoteService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public long addNote(long orderId, Long userId, String noteType, String content) throws SQLException {
		OrderNote note = new OrderNote();
		note.setOrderId(orderId);
		note.setUserId(userId);
		note.setNoteType(noteType);
		note.setContent(content);
		note.setRead(false);
		note.setCreatedAt(LocalDateTime.now());

		if (userId != null) {
			userRepository.findById(userId).ifPresent(user -> {
				note.setSenderName(user.getProfile() != null && user.getProfile().getFullname() != null
					? user.getProfile().getFullname() : user.getUsername());
			});
		} else {
			note.setSenderName("System");
		}

		try {
			return noteDAO.insert(note);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<OrderNote> getNotesByOrderId(long orderId) {
		try {
			List<OrderNote> notes = noteDAO.getByOrderId(orderId);
			for (OrderNote note : notes) {
				if (note.getUserId() != null && note.getSenderName() == null) {
					userRepository.findById(note.getUserId()).ifPresent(user -> {
						note.setSenderName(user.getProfile() != null && user.getProfile().getFullname() != null
							? user.getProfile().getFullname() : user.getUsername());
					});
				}
			}
			return notes;
		} catch (SQLException e) {
			e.printStackTrace();
			return List.of();
		}
	}

	public List<OrderNote> getUnreadNotes(long orderId) {
		try {
			return noteDAO.getUnreadByOrderId(orderId);
		} catch (SQLException e) {
			e.printStackTrace();
			return List.of();
		}
	}

	public int countUnread(long orderId) {
		try {
			return noteDAO.countUnreadByOrderId(orderId);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean markAsRead(long noteId) {
		try {
			return noteDAO.markAsRead(noteId);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean markAllRead(long orderId) {
		try {
			return noteDAO.markAllReadByOrderId(orderId);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteNote(long noteId) {
		try {
			return noteDAO.delete(noteId);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
