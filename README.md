# Colonization
GUI 요소들로 간단히 만드는 우주시대 정착지 개척 시뮬레이션 게임   
여러 플랫폼 연습 겸해 손대고 있는 토이 프로젝트입니다.

# 빌드
## colonization-java-common
Java 기반 빌드의 공통 파트입니다.

## Swing 빌드
Java 의 Swing API 를 활용한 GUI 환경 빌드입니다.   
Java 8 (OpenJDK 1.8 사용 가능) 이상 버전에서 구동되며, GUI 환경이 필요합니다.   
Maven 구동 시 실행 가능한 jar 환경으로 빌드됩니다.   
(colonization-java-common, daemon 를 먼저 Maven 구동 후 Swing 빌드 구동이 가능합니다.)   

## Daemon
Java 기반 Daemon 입니다.
구동 시 시뮬레이션 서버가 Daemon 형태로 구동됩니다.
시뮬레이션 성능이 나오지 않는 클라이언트가, 서버에 접속해 정착지 시뮬레이션을 요청해 사용합니다.
콘솔 프로그램으로, 65246 포트를 기본으로 사용합니다.
콘솔 구동 시 매개변수로 포트 지정이 가능합니다.

(Swing 빌드에 클라이언트 기능 아직 미구현)


# License

   Copyright 2025 HJOW (hujinone22@naver.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
