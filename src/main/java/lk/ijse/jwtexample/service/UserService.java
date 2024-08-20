package lk.ijse.jwtexample.service;

import lk.ijse.jwtexample.dto.UserDTO;

public interface UserService {
    int saveUser(UserDTO userDTO);
    UserDTO searchUser(String userName);
}
