---
description: 사용자가 커밋을 요청하면 변경사항을 분석해 팀 커밋 템플릿에 맞춰 메시지를 작성하고 커밋한다. "커밋해줘", "커밋 메시지 써줘" 같은 요청에 사용.
allowed-tools: Bash(git status *) Bash(git diff *) Bash(git log *) Bash(git branch *) Bash(git commit *)
---

## 현재 상태
- 브랜치: !`git branch --show-current`
- 변경 파일: !`git status --short`
- 최근 커밋: !`git log --oneline -5`

## 커밋 메시지 형식

제목: <prefix>: <요약> (#이슈번호)
(빈 줄)
- 변경 내용 1
- 변경 내용 2

예시:
feat: RotationGizmo 축별 드래그 회전 및 시각적 피드백 구현 (#171)

- X / Y / Z 축 히트 테스트로 드래그 축 판별
- 축별 드래그 방향에 따른 회전량 누적 계산
- drawArc로 드래그 중 회전 가이드 UI 표시
- onDragStart / onRotate 콜백으로 ViewModel 연동

## 규칙
- 타입: feat, fix, refactor, style, test, build, docs, chore, ui, ci 중 하나
- 제목과 본문은 한국어. 함수명, 클래스명 등 코드 식별자는 그대로 써
- 본문은 개조식(명사형 종결)으로 써. "~했다", "~합니다" 금지, 마침표 없이
- 본문 항목은 무엇을 왜 바꿨는지 위주로, 파일 목록 나열은 하지 마
- 이슈 번호는 브랜치 이름에서 찾아. 없으면 커밋하기 전에 물어봐
- Co-Authored-By 같은 트레일러는 붙이지 마

## 진행 순서
1. 스테이징된 변경이 있으면 그것만 대상으로 해. `git diff --staged`로 내용을 확인해
2. 스테이징된 게 없으면 커밋하지 말고 어떤 파일을 포함할지 물어봐. 임의로 `git add .` 하지 마
3. 서로 관련 없는 변경이 섞여 있으면 커밋을 나누자고 제안해
4. 메시지를 보여주고 바로 커밋해. push는 하지 마
5. 커밋 후 커밋 해시와 제목을 알려줘