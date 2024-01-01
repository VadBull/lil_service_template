package org.example.entity.mapper;

import javax.annotation.processing.Generated;
import org.example.entity.User;
import org.example.entity.UserDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-28T16:55:59+0600",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.4.jar, environment: Java 17.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public void updateEntity(UserDto userDto, User user) {
        if ( userDto == null ) {
            return;
        }

        user.setUsername( userDto.getUsername() );
        user.setEmail( userDto.getEmail() );
        user.setPassword( userDto.getPassword() );
    }
}
