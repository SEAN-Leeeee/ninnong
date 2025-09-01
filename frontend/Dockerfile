# 1. Node 이미지를 사용해서 빌드
FROM node:20-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 소스 복사
COPY . .

# 4. 의존성 설치
RUN npm install

# 5. 빌드
RUN npm run build

# 6. 결과물만 남기고 종료 (nginx가 dist 폴더를 사용함)
CMD ["npm", "run", "build"]
