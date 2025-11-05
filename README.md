# Colonization
GUI 요소들로 간단히 만드는 우주시대 정착지 개척 시뮬레이션 게임   
여러 플랫폼 연습 겸해 손대고 있는 토이 프로젝트입니다.

# 실행
Swing 빌드 (기본)를 데스크톱 PC에서 실행해 플레이할 수 있습니다.    
Windows 의 경우 WinLauncher 를 이용해 플레이가 가능합니다.
실행 시, 필요한 경우 JRE를 다운로드 받아 설치하며, (설치 버튼 클릭 필요, 인터넷 필요.)
JRE 확인 이후 게임 (Swing 빌드) 을 최신 버전으로 다운로드 받아 실행합니다.     
자세한 사항은 다음 링크를 참고해 주세요.    
    
https://github.com/HJOW/Colonization/releases/tag/WinLauncher_20251102    
    
기타 플랫폼의 경우    
Java 8 런타임 환경 (OpenJDK 1.8 을 대신 사용 가능) 별도 설치가 필요합니다.
마찬가지로 Swing 빌드 기반 버전입니다.    
자세한 사항은 다음 링크를 참고해 주세요. 이 또한 Windows 에서도 사용이 가능합니다.    
    
https://github.com/HJOW/Colonization/releases/tag/Swing_20251102    

# 게임 언인스톨
더 이상 플레이를 원하지 않아 PC에서 게임 삭제를 원하신다면    
우선 게임 실행파일을 삭제하신 후    
다음 디렉토리 상의 모든 파일을 삭제하시면 됩니다.    

Windows 의 경우 ( [USERNAME] 자리에는 시스템 사용자명이 들어가야 합니다. )    
- C:\Users\\[USERNAME]\\.colonization\    
    
Linux / Mac 의 경우 ( $HOME 은 기본 탑재되는 환경변수입니다. )    
- $HOME/.colonization/    

# 개인정보처리방침
Swing 빌드 (기본) 의 경우 그 어떤 개인정보도 수집하지 않습니다.    
모든 데이터는 로컬 내에서 처리되어 로컬 내에 저장됩니다.    
    
MOD 를 사용하거나, 임의로 소스코드를 수정하여 사용하는 경우는 예외로    
이 경우는 해당 MOD 혹은 수정 소스 제공자의 개인정보처리방침을 확인해 주세요.    
    
Daemon, Servlet 빌드의 경우, 계정 개념이 있어    
가입 시 이름 (닉네임 가능), ID, 암호 (단방향 암호화) 를 입력 받습니다.    
중앙 서버가 없고, 서버 소스 (Servlet 빌드) 를 공개하고 있으며,    
서버 소스인 Servlet 빌드는 아직 완성되지 않았으므로,    
이 역시 개인정보를 아직 수집하지 않는다고 할 수 있습니다.

# 빌드
## colonization-java-group (java-mvn)
여러 Java 기반 구성요소들을 한번에 Maven 으로 관리하기 위한 부모 프로젝트로,    
이 프로젝트 내 pom.xml 에서 버전 등의 공통정보만을 정의할 뿐 그외 소스코드는 없습니다.    
이용 시 Java 8 (OpenJDK 1.8 사용 가능) 및 Maven 3.8 이상 버전이 필요합니다.

## colonization-java-common (java-common)
Java 기반 빌드의 공통 파트입니다.   
Java 8 (OpenJDK 1.8 사용 가능) 및 Maven 3.8 이상 버전이 필요합니다.

## colonization-java-default (java-defaultpack)
Java 기반 빌드 중 기본 제공되는 컨텐츠 파트입니다.    
기본 제공되는 정착지, 도시, 연구, 시설들을 이 프로젝트에서 구현합니다.    
Java 8 (OpenJDK 1.8 사용 가능) 및 Maven 3.8 이상 버전이 필요합니다.

## Swing 빌드 (swing)
Java 의 Swing API 를 활용한 GUI 환경 빌드입니다.   
Java 8 (OpenJDK 1.8 사용 가능) 이상 버전에서 구동되며, GUI 환경이 필요합니다.   
Maven 구동 시 실행 가능한 jar 환경으로 빌드됩니다.   
(colonization-java-common, colonization-java-default, daemon 를 먼저 Maven 구동 후 Swing 빌드 구동이 가능합니다.)   

## WinLauncher
Windows 상에서 Swing 빌드 실행을 쉽게 하기 위한 .Net WPF 기반 프로젝트입니다.    
.Net Framework 4.5.2 기반으로 개발되었습니다.    
(개발 중으로 아직 사용 불가)    

## Servlet 빌드 (java-web)
Java Servlet 2.5, JSTL 1.2 기반의 JSP/Servlet 프로젝트로    
OpenJDK 8, Tomcat 9 기반 하에 동작하는 웹 서비스입니다.    
    
계정 및 회원가입 개념이 존재하며, Swing 빌드에서도 로그인 및 접속이 가능하도록 개발될 예정입니다.
     
## Daemon (daemon)
Java 기반 Daemon 입니다.    
구동 시 시뮬레이션 서버가 Daemon 형태로 구동됩니다.    
시뮬레이션 성능이 나오지 않는 클라이언트가, 서버에 접속해 정착지 시뮬레이션을 요청해 사용하는 방식입니다.    
콘솔 프로그램으로, 65246 포트를 기본으로 사용합니다.    
콘솔 구동 시 매개변수로 포트 지정이 가능합니다.    
    
(Swing 빌드에 클라이언트 기능 아직 미완성, 작업 우선순위 낮음)    
    
## 추가 Pack 개발용 샘플 프로젝트 (java-addpacks)
추가 Pack (새로운 문명 타입, 새 시설과 연구 등 포함) 개발을 위한 샘플 프로젝트입니다.    
Eclipse 사용 개발 시 Kotlin 확장 플러그인이 필요합니다.    
    
## 기타 파일
### packages.zip
WinLauncher 를 Visual Studio 로 불러올 때 필요할 수 있는 파일로, Nuget 로 라이브러리를 설치하며 생성된 packages 폴더를 압축한 파일입니다.    

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

# Using third parties (Dependencies)
다음과 같은 써드 파티 라이브러리와 프레임워크가
사용되었습니다. (또는 사용에 필요합니다.)

## 공통
- 아이콘 이미지         Google Gemini 이미지 생성
- OpenJDK 8             (See https://openjdk.org/legal/                                        )
- Maven 3.8             (See https://maven.apache.org/index.html                               )
- HJOW-Lib              (See https://github.com/HJOW/HJOW-Libs/blob/main/LICENSE               )

## 공통 (선택사항으로, 클래스패스 추가 시 추가 기능 활성화)
- Kotlin libs           (See https://github.com/JetBrains/kotlin-web-site/blob/master/LICENSE  )
- Log4j2                (See https://logging.apache.org/log4j/2.x/index.html                   )
- Apache Commons Codec  (See https://commons.apache.org/proper/commons-codec/                  )
- Apache Commons Lang   (See https://commons.apache.org/proper/commons-lang/                   )

## Swing 빌드만 해당 (모두 선택사항)
- sciss SyntaxPane      (See https://codeberg.org/sciss/SyntaxPane/src/branch/main/LICENSE     )

## Servlet 빌드만 해당
- Servlet API 2.5       (See https://javaee.github.io/servlet-spec/LICENSE                     )
- JSTL 1.2              (See https://javaee.github.io/javaee-spec/LICENSE                      )
- Apache Tomcat 9       (See https://tomcat.apache.org/legal.html                              )
- auth0 / java-jwt      (See https://github.com/auth0/java-jwt/blob/master/LICENSE             )
- HyperSQL              (See https://hsqldb.org/web/hsqlLicense.html                           )
- MyBatis               (See https://mybatis.org/mybatis-3/licenses.html                       )
- JUnit                 (See https://github.com/junit-team/junit4/blob/main/LICENSE-junit.txt  )
- jQuery & jQuery UI    (See https://jquery.com/license/                                       )
- moment.js             (See https://github.com/moment/moment/blob/develop/LICENSE             )
- Babel standalone      (See https://github.com/babel/babel/blob/main/LICENSE                  )
- React                 (See https://github.com/facebook/react/blob/main/LICENSE               )
- Kotlin libs           (See https://github.com/JetBrains/kotlin-web-site/blob/master/LICENSE  )
- Log4j2                (See https://logging.apache.org/log4j/2.x/index.html                   )
- Apache Commons Codec  (See https://commons.apache.org/proper/commons-codec/                  )
- Apache Commons Lang   (See https://commons.apache.org/proper/commons-lang/                   )

## WinLauncher, WinModern 빌드만 해당
- Eclipse Temurin       (See https://adoptium.net/about                                        )
- Newtonsoft.Json       (See https://licenses.nuget.org/MIT                                    )
- NanumGothicCoding     (See https://github.com/naver/nanumfont                                )
- DynamicAero2          (See https://github.com/manju-summoner/DynamicAero2/blob/master/LICENSE)