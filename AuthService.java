package authorization;

public interface AuthService {
    String getNicknameByLoginAndPassword(String login, String password) throws Exception;
//    String changeNicknameByLogin(String login, String oldLogin) throws Exception;

}
