package edu.bju.todos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TodoControllerTests {

    @Autowired
    ApplicationContext context;

    @Test
    public void listSuccess() {

    }

    @Test
    public void listWithSortSuccess() {

    }

    @Test
    public void listWithPageSuccess() {

    }

    @Test
    public void listWithSearchSuccess() {

    }

    @Test
    public void getByIdSuccess() {

    }

    @Test
    public void getByIdFailure() {

    }

    @Test
    public void createUnclassifiedUserSuccess() {

    }

    @Test
    public void createUnclassifiedUserFailure() {

    }

    @Test
    public void createClassifiedUserSuccess() {

    }

    @Test
    public void createClassifiedUserFailure() {

    }

    @Test
    public void createSecretSuccess() {

    }

    @Test
    public void createSecretFailure() {

    }

    @Test
    public void createTopSecretSuccess() {

    }

    @Test
    public void createTopSecretFailure() {

    }

    @Test
    public void createAidFailure() {

    }

    @Test
    public void updateUnclassifiedFailure() {

    }

    @Test
    public void updateClassifiedFailure() {

    }

    @Test
    public void updateSecretFailure() {

    }

    @Test
    public void updateTopSecretFailure() {

    }

    @Test
    public void updateAidSuccess() {

    }

    @Test
    public void updateAidFailure() {

    }

    @Test
    public void deleteUnclassifiedFailure() {

    }

    @Test
    public void deleteClassifiedFailure() {

    }

    @Test
    public void deleteSecretFailure() {

    }

    @Test
    public void deleteTopSecretFailure() {

    }

    @Test
    public void deleteAidSuccess() {

    }

    @Test
    public void deleteAidFailure() {

    }
}
