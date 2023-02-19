package com.zerobase.fastlms.member.service.impl;

import com.zerobase.fastlms.admin.dto.LoginHistoryDto;
import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.entity.LoginHistory;
import com.zerobase.fastlms.admin.mapper.MemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.admin.repository.LoginHistoryRepository;
import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.exception.MemberNotEmailAuthException;
import com.zerobase.fastlms.member.exception.MemberStopUserException;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;
    private final MemberMapper memberMapper;
    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * 회원가입
     */
    @Override
    public Boolean register(MemberInput memberInput) {

        //중복된 아이디는 등록되지 않도록 하는 로직
        Optional<Member> member = memberRepository.findById(memberInput.getUserId());

        if (member.isPresent()) {
            System.out.println("아이디가 존재해");
            //현재 userId에 등록된 아이디가 있음을 의미한다.
            return false;
        }

        //비밀번호 암호화
        String encPassword = PasswordUtils.encPassword(memberInput.getPassword());

        //이메일 인증키
        String emailAuthKey = UUID.randomUUID().toString();

        memberRepository.save(Member.builder()
                .userId(memberInput.getUserId())
                .userName(memberInput.getUserName())
                .phone(memberInput.getPhone())
                .password(encPassword)
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(emailAuthKey)
                .userStatus(Member.MEMBER_STATUS_REQ)
                .adminYn(false)
                .build());

        String email = memberInput.getUserId();
        String subject = "FastLMS 사이트 가입을 축하드립니다.";
        String text = "<p>FastLMS 사이트 가입을 축하드립니다.</p>" +
                "<p>아래 링크를 클릭하셔서 가입을 완료하세요.</p> " +
                "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + emailAuthKey + "'> 계정 활성화 </a></div>";
        mailComponents.sendMail(email, subject, text);
        return true;
    }

    @Override
    public Boolean emailAuth(String uuid) {

        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);
        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        if (member.getEmailAuthYn()) {
            //이미 활성화가 되었기 때문에
            return false;
        }
        member.setUserStatus(Member.MEMBER_STATUS_ING);
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    @Override
    public Boolean sendResetPassword(ResetPasswordInput parameter) {

        Optional<Member> optionalMember = memberRepository
                .findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName());
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        String uuid = UUID.randomUUID().toString();

        member.setResetPasswordKey(uuid);
        member.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "[fastlms] 비밀번호 초기화 메일입니다.";
        String text = "<p>fastlms 비밀번호 초기화 메일입니다.</p>" +
                "<p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p> " +
                "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid + "'> 비밀번호 초기화 링크 </a></div>";
        mailComponents.sendMail(email, subject, text);
        return true;
    }

    @Override
    public Boolean resetPassword(String uuid, String password) {

        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        //초기화 날짜가 유효한 지 체크
        if (member.getResetPasswordLimitDt() == null) {
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }

        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }

        member.setPassword(PasswordUtils.encPassword(password));
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);

        memberRepository.save(member);

        return true;
    }

    @Override
    public Boolean checkResetPassword(String uuid) {
        Optional<Member> optionalMember = memberRepository.findByResetPasswordKey(uuid);
        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        //초기화 날짜가 유효한 지 체크
        if (member.getResetPasswordLimitDt() == null) {
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }

        if (member.getResetPasswordLimitDt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException(" 유효한 날짜가 아닙니다. ");
        }

        return true;
    }

    @Override
    public List<LoginHistoryDto> getLogInfo(String userId) {
        return loginHistoryRepository.findAllByUserIdOrderByLoggedDtDesc(userId)
                .stream()
                .map(LoginHistoryDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberDto> list(MemberParam memberParam) {
        long totalCount = memberMapper.selectListCount(memberParam);
        List<MemberDto> list = memberMapper.selectList(memberParam);

        if (!CollectionUtils.isEmpty(list)) {
            int i = 1;
            for (MemberDto dto : list) {
                dto.setTotalCount(totalCount);
                System.out.println(dto.getUserId());
                LoginHistory history = loginHistoryRepository
                        .findFirstByUserIdOrderByLoggedDtDesc(dto.getUserId())
                        .orElse(null);
                if (history != null) {
                    dto.setLastLoginDt(history.getLoggedDt());
                }
                dto.setSeq(memberParam.getPageStart() + i);
                i++;
            }
        }
        return list;
    }

    @Override
    public MemberDto detail(String userId) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        return optionalMember.map(MemberDto::of).orElse(null);
    }

    @Override
    public Boolean updateStatus(String userId, String userStatus) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();
        member.setUserStatus(userStatus);
        memberRepository.save(member);

        return true;
    }

    @Override
    public Boolean updatePassword(String userId, String password) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();
        member.setPassword(PasswordUtils.encPassword(password));
        memberRepository.save(member);

        return true;
    }

    @Override
    public ServiceResult withdraw(String userId, String password) {

        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        if (!PasswordUtils.equals(password, member.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        member.setUserName("삭제회원");
        member.setPhone("");
        member.setPassword("");
        member.setRegDt(null);
        member.setUpdDt(null);
        member.setEmailAuthYn(false);
        member.setEmailAuthDt(null);
        member.setEmailAuthKey("");
        member.setResetPasswordKey("");
        member.setResetPasswordLimitDt(null);
        member.setUserStatus(Member.MEMBER_STATUS_WITHDRAW);
        member.setZipcode("");
        member.setAddr("");
        member.setAddrDetail("");

        memberRepository.save(member);

        return new ServiceResult(true, "");
    }

    @Override
    public ServiceResult updateMemberPassword(MemberInput parameter) {

        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        if (!PasswordUtils.equals(parameter.getPassword(), member.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }

        member.setPassword(PasswordUtils.encPassword(parameter.getNewPassword()));
        memberRepository.save(member);

        return new ServiceResult(true, "");
    }

    @Override
    public ServiceResult updateMember(MemberInput parameter) {
        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        member.setPhone(parameter.getPhone());
        member.setZipcode(parameter.getZipcode());
        member.setAddr(parameter.getAddr());
        member.setAddrDetail(parameter.getAddrDetail());
        member.setUpdDt(LocalDateTime.now());
        memberRepository.save(member);

        return new ServiceResult(true, "");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //userName이지만 email이 넘어온다.
        Optional<Member> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        //이메일 활성화가 되지 않았으면..
        if (Member.MEMBER_STATUS_REQ.equals(member.getUserStatus())
                && !member.getEmailAuthYn()) {
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인 해주세요");
        }

        if (Member.MEMBER_STATUS_STOP.equals(member.getUserStatus())) {
            throw new MemberStopUserException("정지된 회원입니다.");
        }

        if (Member.MEMBER_STATUS_WITHDRAW.equals(member.getUserStatus())) {
            throw new MemberStopUserException("탈퇴된 회원입니다.");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (member.getAdminYn()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(member.getUserId()
                , member.getPassword()
                , grantedAuthorities);
    }
}