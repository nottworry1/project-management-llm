-- 1. Roles
INSERT INTO role (name, created_at, updated_at)
VALUES ('ROLE_USER', NOW(), NOW()),
       ('ROLE_ADMIN', NOW(), NOW());

-- 2. Users
INSERT INTO users (username, email, password, created_at, updated_at)
VALUES ('alice', 'alice@example.com', 'pass123', NOW(), NOW()),
       ('bob', 'bob@example.com', 'pass123', NOW(), NOW()),
       ('carol', 'carol@example.com', 'pass123', NOW(), NOW()),
       ('admin', 'admin@example.com', 'adminpwd', NOW(), NOW());

-- 3. users_roles
INSERT INTO users_roles (user_id, roles_id)
VALUES
    ((SELECT id FROM users WHERE username = 'alice'),
     (SELECT id FROM role WHERE name = 'ROLE_USER')),
    ((SELECT id FROM users WHERE username = 'bob'),
     (SELECT id FROM role WHERE name = 'ROLE_USER')),
    ((SELECT id FROM users WHERE username = 'carol'),
     (SELECT id FROM role WHERE name = 'ROLE_USER')),
    ((SELECT id FROM users WHERE username = 'admin'),
     (SELECT id FROM role WHERE name = 'ROLE_ADMIN')),
    ((SELECT id FROM users WHERE username = 'admin'),
     (SELECT id FROM role WHERE name = 'ROLE_USER'));

-- 4. Projects
INSERT INTO project (name, description, created_at, updated_at)
VALUES ('Website Redesign', 'Redesign corporate website', NOW(), NOW()),
       ('Mobile App Launch', 'Build and launch new mobile app', NOW(), NOW());

-- 5. project_owners
INSERT INTO project_owners (owners_id, projects_owned_id)
VALUES
    ((SELECT id FROM users WHERE username = 'alice'),
     (SELECT id FROM project WHERE name = 'Website Redesign')),
    ((SELECT id FROM users WHERE username = 'admin'),
     (SELECT id FROM project WHERE name = 'Mobile App Launch'));

-- 6. project_users (members)
INSERT INTO project_users (projects_membered_id, users_id)
VALUES
    ((SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM users WHERE username = 'bob')),
    ((SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM users WHERE username = 'carol')),
    ((SELECT id FROM project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM users WHERE username = 'alice')),
    ((SELECT id FROM project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM users WHERE username = 'bob'));

-- 7. Boards
INSERT INTO board (project_id, name, created_at, updated_at)
VALUES ((SELECT id FROM project WHERE name = 'Website Redesign'), 'UI Board', NOW(), NOW()),
       ((SELECT id FROM project WHERE name = 'Mobile App Launch'), 'API Board', NOW(), NOW());

-- 8. Sprint statuses (we’ll set current_sprint later)
-- 9. Task Statuses
INSERT INTO task_status (name, created_at, updated_at)
VALUES ('To Do', NOW(), NOW()),
       ('In Progress', NOW(), NOW()),
       ('Done', NOW(), NOW());

-- 10. Sprints
INSERT INTO sprint (project_id, board_id, name, description, start_date, end_date, created_at, updated_at,
                    closed, summary)
VALUES
    ((SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM board WHERE name = 'UI Board'),
     'Sprint 1', 'Initial wireframes', NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 7 DAY,
     NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 7 DAY, FALSE, NULL),
    ((SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM board WHERE name = 'UI Board'),
     'Sprint 2', 'Prototype implementation', NOW() - INTERVAL 7 DAY, NOW(),
     NOW() - INTERVAL 7 DAY, NOW(), FALSE, NULL),
    ((SELECT id FROM project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM board WHERE name = 'API Board'),
     'Sprint A', 'API endpoints v1', NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 3 DAY,
     NOW() - INTERVAL 10 DAY, NOW() - INTERVAL 3 DAY, TRUE, 'Great progress!');

-- 11. Link board.current_sprint_id
UPDATE board
SET current_sprint_id =
        (SELECT id FROM sprint WHERE name = 'Sprint 2')
WHERE name = 'UI Board';

UPDATE board
SET current_sprint_id =
        (SELECT id FROM sprint WHERE name = 'Sprint A')
WHERE name = 'API Board';

-- 12. Labels
INSERT INTO label (name, created_at, updated_at)
VALUES ('Bug', NOW(), NOW()),
       ('Feature', NOW(), NOW()),
       ('Chore', NOW(), NOW()),
       ('Documentation', NOW(), NOW());

-- 13. Tasks
INSERT INTO task (title, description, priority, project_id, assignee_id, reporter_id, task_status_id, label_id,
                  story_points, created_at, updated_at)
VALUES
    ('Create homepage mockup', 'Design homepage UI', 'HIGH',
     (SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM users WHERE username = 'bob'),
     (SELECT id FROM users WHERE username = 'alice'),
     (SELECT id FROM task_status WHERE name = 'Done'),
     (SELECT id FROM label WHERE name = 'Feature'),
     5, NOW() - INTERVAL 13 DAY, NOW() - INTERVAL 6 DAY),
    ('Write style guide', 'Document site styles', 'MEDIUM',
     (SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM users WHERE username = 'carol'),
     (SELECT id FROM users WHERE username = 'bob'),
     (SELECT id FROM task_status WHERE name = 'In Progress'),
     (SELECT id FROM label WHERE name = 'Documentation'),
     3, NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 2 DAY),
    ('Implement header component', 'Convert mockup to HTML/CSS', 'HIGH',
     (SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM users WHERE username = 'bob'),
     (SELECT id FROM users WHERE username = 'carol'),
     (SELECT id FROM task_status WHERE name = 'To Do'),
     (SELECT id FROM label WHERE name = 'Chore'),
     5, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY),
    ('Setup CI pipeline', 'Automate builds & tests', 'MEDIUM',
     (SELECT id FROM project WHERE name = 'Website Redesign'),
     (SELECT id FROM users WHERE username = 'alice'),
     (SELECT id FROM users WHERE username = 'bob'),
     (SELECT id FROM task_status WHERE name = 'To Do'),
     (SELECT id FROM label WHERE name = 'Chore'),
     8, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY),
    ('Design auth API', 'JWT-based authentication', 'HIGH',
     (SELECT id FROM project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM users WHERE username = 'alice'),
     (SELECT id FROM users WHERE username = 'admin'),
     (SELECT id FROM task_status WHERE name = 'Done'),
     (SELECT id FROM label WHERE name = 'Feature'),
     5, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 4 DAY),
    ('Document API endpoints', 'Write OpenAPI spec', 'MEDIUM',
     (SELECT id FROM project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM users WHERE username = 'bob'),
     (SELECT id FROM users WHERE username = 'alice'),
     (SELECT id FROM task_status WHERE name = 'Done'),
     (SELECT id FROM label WHERE name = 'Documentation'),
     3, NOW() - INTERVAL 9 DAY, NOW() - INTERVAL 3 DAY);

-- 14. task_sprints: link tasks to sprints
INSERT INTO task_sprints (sprints_id, tasks_id)
VALUES ((SELECT id FROM sprint WHERE name = 'Sprint 1'),
        (SELECT id FROM task WHERE title = 'Create homepage mockup')),
       ((SELECT id FROM sprint WHERE name = 'Sprint 1'),
        (SELECT id FROM task WHERE title = 'Write style guide')),
       ((SELECT id FROM sprint WHERE name = 'Sprint 2'),
        (SELECT id FROM task WHERE title = 'Implement header component')),
       ((SELECT id FROM sprint WHERE name = 'Sprint 2'),
        (SELECT id FROM task WHERE title = 'Setup CI pipeline')),
       ((SELECT id FROM sprint WHERE name = 'Sprint A'),
        (SELECT id FROM task WHERE title = 'Design auth API')),
       ((SELECT id FROM sprint WHERE name = 'Sprint A'),
        (SELECT id FROM task WHERE title = 'Document API endpoints'));

-- 15. Comments
INSERT INTO comment (task_id, user_id, text, created_at, updated_at)
VALUES
    ((SELECT id FROM task WHERE title = 'Write style guide'),
     (SELECT id FROM users WHERE username = 'carol'),
     'Blocked: waiting on design approval', NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY),
    ((SELECT id FROM task WHERE title = 'Implement header component'),
     (SELECT id FROM users WHERE username = 'bob'),
     'Blocker: CSS variables not defined', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
    ((SELECT id FROM task WHERE title = 'Create homepage mockup'),
     (SELECT id FROM users WHERE username = 'alice'),
     'Looks good!', NOW() - INTERVAL 12 DAY, NOW() - INTERVAL 12 DAY),
    ((SELECT id FROM task WHERE title = 'Design auth API'),
     (SELECT id FROM users WHERE username = 'admin'),
     'Please review the security model', NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY);