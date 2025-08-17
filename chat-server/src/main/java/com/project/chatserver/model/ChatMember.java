package com.project.chatserver.model;

import java.util.ArrayList;
import java.util.List;

import com.project.chatserver.constants.MemberRole;

import lombok.*;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder // @Builder 대신 @SuperBuilder 사용
@Table(name="member", schema = "chat",
		indexes = {
				@jakarta.persistence.Index(name = "idx_member_email", columnList = "email"),
				@jakarta.persistence.Index(name = "idx_member_nickname", columnList = "nickname")
		},
		uniqueConstraints = {
				@jakarta.persistence.UniqueConstraint(name = "uk_member_email", columnNames = "email"),
				@jakarta.persistence.UniqueConstraint(name = "uk_member_nickname", columnNames = "nickname")
		}		
)
	public class ChatMember {

		

	@Id
	@Column(name = "member_id")
	private Long id;

	private String email; // 이메일
	//private String password; // 비밀번호
	private String nickname; // 닉네임
	private String imageUrl; // 프로필 이미지

	@Enumerated(EnumType.STRING)
	private MemberRole role;

	//@Enumerated(EnumType.STRING)
	//private SocialType socialType; // KAKAO, NAVER, GOOGLE

	//private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

	@Builder.Default
	@OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ParticipantChatRoom> roomList =  new ArrayList<ParticipantChatRoom>();



}
