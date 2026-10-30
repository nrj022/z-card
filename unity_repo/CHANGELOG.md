# Unity Library Changelog

`com.zcard:unity` AAR 버전별 변경 이력

- Unity 프로젝트: [nrj022/z-card-unity](https://github.com/nrj022/z-card-unity)
- 기준 커밋: AAR을 export한 Unity 저장소 커밋 해시
- 새 버전 추가 시 `gradle/libs.versions.toml`의 `unity` 버전과 함께 갱신

## [1.0.0] - 2026-10-30

기준 커밋: `910d885`  
이전 AAR: `libs/unityLibrary-release.aar` (#176)

- m_017 모델을 rabbit-v3 프리팹으로 교체
- bg_003 HDR 배경 갱신
- Mobile 기능 묶음(`com.unity.feature.mobile`) 제거
  - Android Logcat, Adaptive Performance, Mobile Notifications 패키지 제외
  - `mobilenotifications.androidlib` 모듈 의존성 제거
- flatDir AAR 대신 로컬 Maven 저장소(`unity_repo`)로 배포
- `mainTemplate.gradle`에 `maven-publish` 설정 추가 (`com.zcard:unity`)
- 전이 의존성: `androidx.games:games-frame-pacing:2.1.2`
