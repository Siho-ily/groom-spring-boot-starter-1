DROP TABLE IF EXISTS tech_stack;
DROP TABLE IF EXISTS profile;
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS member;

-- [추가] 회원 테이블
CREATE TABLE member (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL,
                        role VARCHAR(20) NOT NULL DEFAULT 'USER',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- [추가] Refresh Token 테이블
CREATE TABLE refresh_token (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               member_id BIGINT NOT NULL UNIQUE,
                               token VARCHAR(500) NOT NULL,
                               expiry_date TIMESTAMP NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

CREATE TABLE profile (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         member_id BIGINT NOT NULL,                          -- [추가] FK → member
                         name VARCHAR(50) NOT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         bio VARCHAR(500),
                         position VARCHAR(20) NOT NULL,
                         career_years INT NOT NULL DEFAULT 0,
                         github_url VARCHAR(200),
                         blog_url VARCHAR(200),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (member_id) REFERENCES member(id)       -- [추가]
);

CREATE TABLE tech_stack (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            profile_id BIGINT NOT NULL,
                            name VARCHAR(50) NOT NULL,
                            category VARCHAR(20) NOT NULL,
                            proficiency VARCHAR(20) NOT NULL,
                            years_of_exp INT NOT NULL DEFAULT 0,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE
);

CREATE INDEX idx_profile_member_id ON profile(member_id);
CREATE INDEX idx_profile_position ON profile(position);
CREATE INDEX idx_tech_stack_profile_id ON tech_stack(profile_id);
CREATE INDEX idx_tech_stack_category ON tech_stack(category);