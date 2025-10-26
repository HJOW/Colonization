# Colonization
GUI 요소들로 간단히 만드는 우주시대 정착지 개척 시뮬레이션 게임   
여러 플랫폼 연습 겸해 손대고 있는 토이 프로젝트입니다.

# 실행
Swing 빌드를 데스크톱 PC에서 실행해 플레이할 수 있습니다.   
Java 8 런타임 환경 (OpenJDK 1.8 을 대신 사용 가능) 설치가 필요합니다.

# 빌드
## colonization-java-common (java-common)
Java 기반 빌드의 공통 파트입니다.   
Java 8 (OpenJDK 1.8 사용 가능) 및 Maven 3.8 이상 버전이 필요합니다.

## colonization-java-default (java-defaultpack)
Java 기반 빌드 중 기본 제공되는 컨텐츠 파트입니다.
기본 제공되는 정착지, 도시, 연구, 시설들을 구현합니다.
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
용량 문제로 CefSharp 는 제외되어 있습니다. [패키지](http://hjow.duckdns.org/colonization/packages.zip)

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
- OpenJDK 8          (See https://openjdk.org/legal/                                       )
- Apache Tomcat 9    (See https://tomcat.apache.org/legal.html                             )
- Maven 3.8          (See https://maven.apache.org/index.html                              )

## Servlet 빌드만 해당
- Servlet API 2.5    (See https://javaee.github.io/servlet-spec/LICENSE                    )
- JSTL 1.2           (See https://javaee.github.io/javaee-spec/LICENSE                     )
- auth0 / java-jwt   (See https://github.com/auth0/java-jwt/blob/master/LICENSE            )
- HJOW-Lib           (See https://github.com/HJOW/HJOW-Libs/blob/main/LICENSE              )
- HyperSQL           (See https://hsqldb.org/web/hsqlLicense.html                          )
- MyBatis            (See https://mybatis.org/mybatis-3/licenses.html                      )
- Kotlin stdlib      (See https://github.com/JetBrains/kotlin-web-site/blob/master/LICENSE )
- Log4j2             (See https://logging.apache.org/log4j/2.x/index.html                  )
- JUnit              (See https://github.com/junit-team/junit4/blob/main/LICENSE-junit.txt )
- jQuery & jQuery UI (See https://jquery.com/license/                                      )
- moment.js          (See https://github.com/moment/moment/blob/develop/LICENSE            )
- Babel standalone   (See https://github.com/babel/babel/blob/main/LICENSE                 )
- React              (See https://github.com/facebook/react/blob/main/LICENSE              )

## WinLauncher 빌드만 해당
- Eclipse Temurin    (See https://adoptium.net/about                                       )
- Cefsharp           (See https://www.nuget.org/packages/CefSharp.Wpf/140.1.140/license    )
- Newtonsoft.Json    (See https://licenses.nuget.org/MIT                                   )
- NanumGothicCoding  (See https://github.com/naver/nanumfont                               )