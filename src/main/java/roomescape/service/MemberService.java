package roomescape.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import roomescape.domain.Member;
import roomescape.domain.repository.MemberRepository;
import roomescape.exception.member.AuthenticationFailureException;
import roomescape.exception.member.DuplicatedEmailException;
import roomescape.service.security.JwtUtils;
import roomescape.web.dto.request.LoginRequest;
import roomescape.web.dto.request.SignupRequest;
import roomescape.web.dto.response.MemberResponse;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public List<MemberResponse> findAllMember() {
        return memberRepository.findAll()
                .stream()
                .map(member -> new MemberResponse(member.getName()))
                .toList();
    }

    public String login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmailAndPassword(loginRequest.email(), loginRequest.password())
                .orElseThrow(AuthenticationFailureException::new);

        return "token=" + JwtUtils.encode(findMember);
    }

    public long signup(SignupRequest signupRequest) {
        checkDuplicateEmail(signupRequest.email());
        Member savedMember = memberRepository.save(signupRequest.toMember());
        return savedMember.getId();
    }

    private void checkDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException();
        }
    }

    //TODO
    public void withdrawal(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(IllegalArgumentException::new);

        memberRepository.delete(findMember);
    }
}
