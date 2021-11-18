package qna.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
public class UserTest {
    public static final User JAVAJIGI = new User( 1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    @Autowired
    UserRepository userRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    DeleteHistoryRepository deleteHistoryRepository;

    User user;

    @BeforeEach
    void init() {
        user = TestUserFactory.create();
    }

    @Test
    void 저장() {
        //when
        User savedUser = save(user);

        // then
        assertAll(
                () -> assertThat(savedUser.getId()).isNotNull(),
                () -> assertThat(savedUser.getUserId()).isEqualTo(user.getUserId())
        );
    }

    @Test
    void 검색() {
        // given
        User savedUser = save(user);

        //when
        User foundUser = userRepository.findById(savedUser.getId()).get();

        //then
        assertThat(foundUser).isEqualTo(savedUser);
    }

    @Test
    void 연관관계_답변_조회() {
        // given
        Answer answer = TestAnswerFactory.create();
        answerRepository.save(answer);

        // when
        user.addAnswer(answer);

        // then
        assertThat(user.getAnswers().get(0).getId()).isEqualTo(answer.getId());
    }

    @Test
    void 연관관계_질문_조회() {
        // given
        Question question = questionRepository.save(TestQuestionFactory.create());

        // when
        user.addAQuestion(question);

        // then
        assertThat(user.getQuestions().get(0).getId()).isEqualTo(question.getId());
    }

    @Test
    void 연관관계_삭제히스토리_조회() {
        // given
        Question question = questionRepository.save(TestQuestionFactory.create());
        DeleteHistory deleteHistory = deleteHistoryRepository.save(TestDeleteHistoryFactory.create(ContentType.QUESTION, question.getId(), user));

        // when
        user.addDeleteHistory(deleteHistory);

        // then
        assertThat(user.getDeleteHistories().get(0).getId()).isEqualTo(deleteHistory.getId());
    }

    @Test
    void 수정() {
        // when
        user.setEmail("test@gmail.com");

        // then
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void 삭제() {
        // given
        save(user);

        // when
        userRepository.delete(user);

        // then
        assertThat(userRepository.findById(user.getId()).isPresent()).isFalse();
    }

    private User save(User user) {
        return userRepository.save(user);
    }
}
