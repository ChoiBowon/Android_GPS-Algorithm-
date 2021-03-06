2017 Android GPS Project 

본 프로젝트는 적은 빈도 수의 위치 정보 수집으로 빠르게 사용자의 건물 출입을 판단하는 것을 목표로 진행하였다. 
이를 위한 최적 알고리즘 모델링으로 인공위성 수, 위치 제공자의 정확도, 위치 제공자의 수집 횟수를 활용하였다. 위치 정보가 수집되는 주기는 5초로 설정하였다. 
결과적으로 실내에서 실외로 나가는 경우, 판단에 소요되는 시간은 대략 15초가 걸렸으며, 제한 조건 횟수를 3번으로 둔 것을 기반으로 15초는 매우 빠르게 판단하는 것으로 볼 수 있다. 
그리고 실외에서 실내로 들어오는 경우는 1분 내외의 결과를 도출하였다. 
GPS를 감지하는 속도가 Network 를 감지하는 속도보다 빠른 점을 고려했을 때, 실외에서 실내로 들어오는 경우에 걸리는 속도가 더 큰 것이 사실이다. 

만약 수집 시간을 5초 이상으로 늘린다면 판단시간은 15초 이상으로 늘어나게 될 것이다. 반대로 수집시간을 줄인다면 15초 미만으로 더 빨리 건물 출입을 판단하겠지만, 측정하는 빈도수가 높아지게 되므로 배터리 소모가 커질 수 밖에 없을 것이다.
