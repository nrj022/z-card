---
description: Z Card 앱의 baseVersion을 지정한 버전으로 올리고 develop 브랜치에 커밋
argument-hint: [버전 예: 1.3.0]
disable-model-invocation: true
allowed-tools: Bash(git status *) Bash(git branch *) Bash(git add app/build.gradle.kts) Bash(git commit *)
---

## 현재 상태
- 브랜치: !`git branch --show-current`
- 변경 파일: !`git status --short`

## Instructions
목표 버전: $ARGUMENTS

1. 목표 버전이 비어 있으면 작업하지 말고 버전을 물어봐.
   입력에 v가 붙어 있든 없든 숫자 부분(예: 1.3.0)만 사용해. 
2. 변경하려는 버전과 기존 버전이 같으면 작업하지 말고 알려줘. 
3. 현재 브랜치가 develop이 아니면 작업하지 말고 알려줘. 
4. app/build.gradle.kts의 baseVersion 값을 바꿔. 형식: "1.3.0"
5. app/build.gradle.kts에 이미 다른 변경이 있으면 작업하지 말고 알려줘. 없으면 이 파일만 스테이징해. 다른 파일은 건드리지 마.
6. 커밋 제목: build: 앱 기본 버전 1.3.0으로 업데이트 
7. push는 하지 마. 커밋 후 커밋 해시와 바뀐 줄을 보여줘.