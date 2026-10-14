---
description: Z Card develop 브랜치의 baseVersion으로 git 태그 생성
disable-model-invocation: true
allowed-tools: Bash(git branch *) Bash(git status *) Bash(git show HEAD:app/build.gradle.kts) Bash(git tag *) Bash(git log *)
---

## 현재 상태
- 브랜치: !`git branch --show-current`
- 변경 파일: !`git status --short`

## Instructions
1. 현재 브랜치가 develop이 아니면 작업하지 말고 알려줘.
2. `git show HEAD:app/build.gradle.kts`로 마지막 커밋 기준 baseVersion 값을 읽어.
3. app/build.gradle.kts에 커밋 안 된 변경이 있으면 작업하지 말고 알려줘.
4. 태그 이름은 v + baseVersion이야 (예: v1.3.0).
5. 같은 이름의 태그가 이미 있으면 만들지 말고 알려줘. 절대 -f로 덮어쓰지 마.
6. `git tag -a v1.3.0 -m "v1.3.0 릴리즈"`으로 현재 커밋에 태그를 만들어.
7. push는 하지 마. 태그 이름, 태그가 달린 커밋 해시와 커밋 제목을 보여줘.