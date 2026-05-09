-- Create Categories Table
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    bucket VARCHAR(50) NOT NULL -- INCOME, NEED, WANT, SAVING, UNCATEGORIZED
);

-- Create Period Entries Table
CREATE TABLE period_entries (
    id SERIAL PRIMARY KEY,
    period_id INTEGER NOT NULL REFERENCES spending_periods(id) ON DELETE CASCADE,
    category_id INTEGER NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    amount DOUBLE PRECISION DEFAULT 0.0,
    UNIQUE(period_id, category_id)
);

-- Pre-populate Categories with existing mapping
INSERT INTO categories (name, bucket) VALUES
('Salary', 'INCOME'),
('Other income', 'INCOME'),
('Fabio contributions', 'INCOME'),
('Mortgage', 'NEED'),
('Bills', 'NEED'),
('Groceries', 'NEED'),
('Transport', 'NEED'),
('Personal care', 'NEED'),
('Dentist', 'NEED'),
('Expenses', 'NEED'),
('Eating out', 'WANT'),
('Shopping', 'WANT'),
('Entertainment', 'WANT'),
('Books', 'WANT'),
('Clothing', 'WANT'),
('Gifts', 'WANT'),
('Tech', 'WANT'),
('Drinks', 'WANT'),
('Holidays', 'WANT'),
('Lego', 'WANT'),
('Gaming', 'WANT'),
('Comics', 'WANT'),
('Psycotherapy', 'WANT'),
('Gym', 'WANT'),
('Cycling', 'WANT'),
('Culture', 'WANT'),
('Parents', 'WANT'),
('Savings', 'SAVING'),
('Investment', 'SAVING'),
('SIPP', 'SAVING');

-- Data Migration from flat columns to period_entries
-- Salary
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.salary FROM spending_periods sp, categories c WHERE c.name = 'Salary';

-- Other Income
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.other_income FROM spending_periods sp, categories c WHERE c.name = 'Other income';

-- Partner Contributions
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.partner_contributions FROM spending_periods sp, categories c WHERE c.name = 'Fabio contributions';

-- Mortgage
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.mortgage FROM spending_periods sp, categories c WHERE c.name = 'Mortgage';

-- Bills
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.bills FROM spending_periods sp, categories c WHERE c.name = 'Bills';

-- Groceries
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.groceries FROM spending_periods sp, categories c WHERE c.name = 'Groceries';

-- Transport
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.transport FROM spending_periods sp, categories c WHERE c.name = 'Transport';

-- Personal care
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.personal_care FROM spending_periods sp, categories c WHERE c.name = 'Personal care';

-- Dentist
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.dentist FROM spending_periods sp, categories c WHERE c.name = 'Dentist';

-- Expenses
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.expenses FROM spending_periods sp, categories c WHERE c.name = 'Expenses';

-- Eating out
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.eating_out FROM spending_periods sp, categories c WHERE c.name = 'Eating out';

-- Shopping
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.shopping FROM spending_periods sp, categories c WHERE c.name = 'Shopping';

-- Entertainment
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.entertainment FROM spending_periods sp, categories c WHERE c.name = 'Entertainment';

-- Books
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.books FROM spending_periods sp, categories c WHERE c.name = 'Books';

-- Clothing
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.clothing FROM spending_periods sp, categories c WHERE c.name = 'Clothing';

-- Gifts
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.gifts FROM spending_periods sp, categories c WHERE c.name = 'Gifts';

-- Tech
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.tech FROM spending_periods sp, categories c WHERE c.name = 'Tech';

-- Drinks
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.drinks FROM spending_periods sp, categories c WHERE c.name = 'Drinks';

-- Holidays
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.holidays FROM spending_periods sp, categories c WHERE c.name = 'Holidays';

-- Lego
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.lego FROM spending_periods sp, categories c WHERE c.name = 'Lego';

-- Gaming
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.gaming FROM spending_periods sp, categories c WHERE c.name = 'Gaming';

-- Comics
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.comics FROM spending_periods sp, categories c WHERE c.name = 'Comics';

-- Psycotherapy
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.psychotherapy FROM spending_periods sp, categories c WHERE c.name = 'Psycotherapy';

-- Gym
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.gym FROM spending_periods sp, categories c WHERE c.name = 'Gym';

-- Cycling
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.cycling FROM spending_periods sp, categories c WHERE c.name = 'Cycling';

-- Culture
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.culture FROM spending_periods sp, categories c WHERE c.name = 'Culture';

-- Parents
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.parents FROM spending_periods sp, categories c WHERE c.name = 'Parents';

-- Savings
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.savings FROM spending_periods sp, categories c WHERE c.name = 'Savings';

-- Investment
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.investment FROM spending_periods sp, categories c WHERE c.name = 'Investment';

-- SIPP
INSERT INTO period_entries (period_id, category_id, amount)
SELECT sp.id, c.id, sp.sipp FROM spending_periods sp, categories c WHERE c.name = 'SIPP';

-- Drop old columns from spending_periods
ALTER TABLE spending_periods 
DROP COLUMN salary,
DROP COLUMN other_income,
DROP COLUMN partner_contributions,
DROP COLUMN mortgage,
DROP COLUMN bills,
DROP COLUMN groceries,
DROP COLUMN transport,
DROP COLUMN personal_care,
DROP COLUMN dentist,
DROP COLUMN expenses,
DROP COLUMN eating_out,
DROP COLUMN shopping,
DROP COLUMN entertainment,
DROP COLUMN books,
DROP COLUMN clothing,
DROP COLUMN gifts,
DROP COLUMN tech,
DROP COLUMN drinks,
DROP COLUMN holidays,
DROP COLUMN lego,
DROP COLUMN gaming,
DROP COLUMN comics,
DROP COLUMN psychotherapy,
DROP COLUMN gym,
DROP COLUMN cycling,
DROP COLUMN culture,
DROP COLUMN parents,
DROP COLUMN savings,
DROP COLUMN investment,
DROP COLUMN sipp;
