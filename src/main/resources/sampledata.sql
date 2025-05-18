-- 1. Roles
-- INSERT INTO public.role (name, created_at, updated_at)
-- VALUES ('ROLE_USER', NOW(), NOW()),
--        ('ROLE_ADMIN', NOW(), NOW());

-- 2. Users
-- INSERT INTO public.users (username, email, password, created_at, updated_at)
-- VALUES ('alice', 'alice@example.com', 'pass123', NOW(), NOW()),
--        ('bob', 'bob@example.com', 'pass123', NOW(), NOW()),
--        ('carol', 'carol@example.com', 'pass123', NOW(), NOW()),
--        ('admin', 'admin@example.com', 'adminpwd', NOW(), NOW());

-- 3. users_roles
INSERT INTO public.users_roles (user_id, roles_id)
VALUES
    -- alice → USER
    ((SELECT id FROM public.users WHERE username = 'alice'),
     (SELECT id FROM public.role WHERE name = 'ROLE_USER')),
    -- bob → USER
    ((SELECT id FROM public.users WHERE username = 'bob'),
     (SELECT id FROM public.role WHERE name = 'ROLE_USER')),
    -- carol → USER
    ((SELECT id FROM public.users WHERE username = 'carol'),
     (SELECT id FROM public.role WHERE name = 'ROLE_USER')),
    -- admin → ADMIN & USER
    ((SELECT id FROM public.users WHERE username = 'admin'),
     (SELECT id FROM public.role WHERE name = 'ROLE_ADMIN')),
    ((SELECT id FROM public.users WHERE username = 'admin'),
     (SELECT id FROM public.role WHERE name = 'ROLE_USER'));

-- 4. Projects
INSERT INTO public.project (name, description, created_at, updated_at)
VALUES ('Website Redesign', 'Redesign corporate website', NOW(), NOW()),
       ('Mobile App Launch', 'Build and launch new mobile app', NOW(), NOW());

-- 5. project_owners
INSERT INTO public.project_owners (owners_id, projects_owned_id)
VALUES
    -- Alice owns Website
    ((SELECT id FROM public.users WHERE username = 'alice'),
     (SELECT id FROM public.project WHERE name = 'Website Redesign')),
    -- Admin owns Mobile App
    ((SELECT id FROM public.users WHERE username = 'admin'),
     (SELECT id FROM public.project WHERE name = 'Mobile App Launch'));

-- 6. project_users (members)
INSERT INTO public.project_users (projects_membered_id, users_id)
VALUES
    -- Bob and Carol join Website
    ((SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.users WHERE username = 'bob')),
    ((SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.users WHERE username = 'carol')),
    -- Alice and Bob join Mobile App
    ((SELECT id FROM public.project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM public.users WHERE username = 'alice')),
    ((SELECT id FROM public.project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM public.users WHERE username = 'bob'));

-- 7. Boards
INSERT INTO public.board (project_id, name, created_at, updated_at)
VALUES ((SELECT id FROM public.project WHERE name = 'Website Redesign'), 'UI Board', NOW(), NOW()),
       ((SELECT id FROM public.project WHERE name = 'Mobile App Launch'), 'API Board', NOW(), NOW());

-- 8. Sprint statuses (we’ll set current_sprint later)
-- 9. Task Statuses
-- INSERT INTO public.task_status (name, created_at, updated_at)
-- VALUES ('To Do', NOW(), NOW()),
--        ('In Progress', NOW(), NOW()),
--        ('Done', NOW(), NOW());

-- 10. Sprints
INSERT INTO public.sprint (project_id, board_id, name, description, start_date, end_date, created_at, updated_at,
                           closed, ai_summary, summary)
VALUES
    -- Two sprints for Website
    ((SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.board WHERE name = 'UI Board'),
     'Sprint 1', 'Initial wireframes', NOW() - INTERVAL '14 days', NOW() - INTERVAL '7 days',
     NOW() - INTERVAL '14 days', NOW() - INTERVAL '7 days', FALSE, NULL, NULL),
    ((SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.board WHERE name = 'UI Board'),
     'Sprint 2', 'Prototype implementation', NOW() - INTERVAL '7 days', NOW(),
     NOW() - INTERVAL '7 days', NOW(), FALSE, NULL, NULL),
    -- One sprint for Mobile App
    ((SELECT id FROM public.project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM public.board WHERE name = 'API Board'),
     'Sprint A', 'API endpoints v1', NOW() - INTERVAL '10 days', NOW() - INTERVAL '3 days',
     NOW() - INTERVAL '10 days', NOW() - INTERVAL '3 days', TRUE, 'Great progress!', NULL);

-- 11. Link board.current_sprint_id
UPDATE public.board
SET current_sprint_id =
            (SELECT id FROM public.sprint WHERE name = 'Sprint 2')
WHERE name = 'UI Board';

UPDATE public.board
SET current_sprint_id =
            (SELECT id FROM public.sprint WHERE name = 'Sprint A')
WHERE name = 'API Board';

-- 12. Labels
INSERT INTO public.label (name, created_at, updated_at)
VALUES ('Bug', NOW(), NOW()),
       ('Feature', NOW(), NOW()),
       ('Chore', NOW(), NOW()),
       ('Documentation', NOW(), NOW());

-- 13. Tasks
INSERT INTO public.task (title, description, priority, project_id, assignee_id, reporter_id, task_status_id, label_id,
                         story_points, created_at, updated_at)
VALUES
    -- Website Sprint 1 tasks
    ('Create homepage mockup', 'Design homepage UI', 'HIGH',
     (SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.users WHERE username = 'bob'),
     (SELECT id FROM public.users WHERE username = 'alice'),
     (SELECT id FROM public.task_status WHERE name = 'Done'),
     (SELECT id FROM public.label WHERE name = 'Feature'),
     5, NOW() - INTERVAL '13 days', NOW() - INTERVAL '6 days'),
    ('Write style guide', 'Document site styles', 'MEDIUM',
     (SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.users WHERE username = 'carol'),
     (SELECT id FROM public.users WHERE username = 'bob'),
     (SELECT id FROM public.task_status WHERE name = 'In Progress'),
     (SELECT id FROM public.label WHERE name = 'Documentation'),
     3, NOW() - INTERVAL '12 days', NOW() - INTERVAL '2 days'),
    -- Website Sprint 2 tasks
    ('Implement header component', 'Convert mockup to HTML/CSS', 'HIGH',
     (SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.users WHERE username = 'bob'),
     (SELECT id FROM public.users WHERE username = 'carol'),
     (SELECT id FROM public.task_status WHERE name = 'To Do'),
     (SELECT id FROM public.label WHERE name = 'Chore'),
     5, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
    ('Setup CI pipeline', 'Automate builds & tests', 'MEDIUM',
     (SELECT id FROM public.project WHERE name = 'Website Redesign'),
     (SELECT id FROM public.users WHERE username = 'alice'),
     (SELECT id FROM public.users WHERE username = 'bob'),
     (SELECT id FROM public.task_status WHERE name = 'To Do'),
     (SELECT id FROM public.label WHERE name = 'Chore'),
     8, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
    -- Mobile App Sprint A tasks
    ('Design auth API', 'JWT-based authentication', 'HIGH',
     (SELECT id FROM public.project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM public.users WHERE username = 'alice'),
     (SELECT id FROM public.users WHERE username = 'admin'),
     (SELECT id FROM public.task_status WHERE name = 'Done'),
     (SELECT id FROM public.label WHERE name = 'Feature'),
     5, NOW() - INTERVAL '9 days', NOW() - INTERVAL '4 days'),
    ('Document API endpoints', 'Write OpenAPI spec', 'MEDIUM',
     (SELECT id FROM public.project WHERE name = 'Mobile App Launch'),
     (SELECT id FROM public.users WHERE username = 'bob'),
     (SELECT id FROM public.users WHERE username = 'alice'),
     (SELECT id FROM public.task_status WHERE name = 'Done'),
     (SELECT id FROM public.label WHERE name = 'Documentation'),
     3, NOW() - INTERVAL '9 days', NOW() - INTERVAL '3 days');

-- 14. task_sprints: link tasks to sprints
INSERT INTO public.task_sprints (sprints_id, tasks_id)
VALUES ((SELECT id FROM public.sprint WHERE name = 'Sprint 1'),
        (SELECT id FROM public.task WHERE title = 'Create homepage mockup')),
       ((SELECT id FROM public.sprint WHERE name = 'Sprint 1'),
        (SELECT id FROM public.task WHERE title = 'Write style guide')),
       ((SELECT id FROM public.sprint WHERE name = 'Sprint 2'),
        (SELECT id FROM public.task WHERE title = 'Implement header component')),
       ((SELECT id FROM public.sprint WHERE name = 'Sprint 2'),
        (SELECT id FROM public.task WHERE title = 'Setup CI pipeline')),
       ((SELECT id FROM public.sprint WHERE name = 'Sprint A'),
        (SELECT id FROM public.task WHERE title = 'Design auth API')),
       ((SELECT id FROM public.sprint WHERE name = 'Sprint A'),
        (SELECT id FROM public.task WHERE title = 'Document API endpoints'));

-- 15. Comments
INSERT INTO public.comment (task_id, user_id, text, created_at, updated_at)
VALUES
    -- some blocker comments
    ((SELECT id FROM public.task WHERE title = 'Write style guide'),
     (SELECT id FROM public.users WHERE username = 'carol'),
     'Blocked: waiting on design approval', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
    ((SELECT id FROM public.task WHERE title = 'Implement header component'),
     (SELECT id FROM public.users WHERE username = 'bob'),
     'Blocker: CSS variables not defined', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
    -- normal comments
    ((SELECT id FROM public.task WHERE title = 'Create homepage mockup'),
     (SELECT id FROM public.users WHERE username = 'alice'),
     'Looks good!', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
    ((SELECT id FROM public.task WHERE title = 'Design auth API'),
     (SELECT id FROM public.users WHERE username = 'admin'),
     'Please review the security model', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days');
