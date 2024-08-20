package lk.ijse.jwtexample.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.jwtexample.dto.UserDTO;
import lk.ijse.jwtexample.entity.User;
import lk.ijse.jwtexample.repository.UserRepository;
import lk.ijse.jwtexample.service.UserService;
import lk.ijse.jwtexample.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService ,UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),getAuthority(user));
    }
    @Override
    public int saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())){
            return VarList.Not_Acceptable;
        }else {
//            to hidden password
            BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userDTO.setRole("DASH_ADMIN");
            userRepository.save(modelMapper.map(userDTO,User.class));
            return VarList.Created;
        }
    }


    public UserDTO loadUserDetailsByUsername(String userName) throws UsernameNotFoundException {
            User user = userRepository.findByEmail(userName);
            return modelMapper.map(user,UserDTO.class);

    }

    private Set<SimpleGrantedAuthority> getAuthority(User user){
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    @Override
    public UserDTO searchUser(String userName) {
        if (userRepository.existsByEmail(userName)){
            User user = userRepository.findByEmail(userName);
            return modelMapper.map(user,UserDTO.class);
        }else {
            return null;
        }
    }
}
