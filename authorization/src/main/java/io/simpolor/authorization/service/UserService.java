package io.simpolor.authorization.service;

import io.simpolor.authorization.repository.UserRepository;
import io.simpolor.authorization.repository.entity.User;
import io.simpolor.authorization.security.PasswordEncoding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final PasswordEncoding passwordEncoding;
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<User> optionalUser = userRepository.findByEmail(email);
		if(!optionalUser.isPresent()){
			throw new UsernameNotFoundException("Exist Not User");
		}

		return optionalUser.get();
	}

	public User get(long seq) {

		Optional<User> optionalUser = userRepository.findById(seq);
		if(!optionalUser.isPresent()){
			throw new EntityNotFoundException("Not found User");
		}

		return optionalUser.get();
	}

	public void insert(User user) {

		user.setPassword(passwordEncoding.encode(user.getPassword()));
		userRepository.save(user);
	}

	public void update(User user) {

		Optional<User> optionalUser = userRepository.findById(user.getSeq());
		if(!optionalUser.isPresent()){
			throw new EntityNotFoundException("Not found User");
		}
		userRepository.save(user);
	}

	public void delete(long seq) {

		Optional<User> optionalUser = userRepository.findById(seq);
		if(!optionalUser.isPresent()){
			throw new EntityNotFoundException("Not found User");
		}
		userRepository.deleteById(seq);
	}
}
