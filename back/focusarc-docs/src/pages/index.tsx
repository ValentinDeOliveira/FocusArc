import type {ReactNode} from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import Heading from '@theme/Heading';
import styles from './index.module.css';

const features = [
  {
    icon: '🗂️',
    title: 'Arc → Chapter → Task',
    description:
      'Organise work in a three-level hierarchy. An Arc spans weeks or months, each Chapter represents one day, and Tasks are the concrete work blocks you schedule and execute.',
  },
  {
    icon: '⏱️',
    title: 'Overtime Mode',
    description:
      'When a task timer reaches its estimated end, it silently enters overtime — no pop-ups, no interruptions. Your actual time (including overtime) is recorded when you stop.',
  },
  {
    icon: '📊',
    title: 'Time Tracking & Analytics',
    description:
      'See how you spend your time at both daily and arc level. Visualise productivity and estimation accuracy over time to improve your planning.',
  },
];

function Feature({icon, title, description}: {icon: string; title: string; description: string}) {
  return (
    <div className={clsx('col col--4', styles.feature)}>
      <div className={styles.featureIcon}>{icon}</div>
      <Heading as="h3">{title}</Heading>
      <p>{description}</p>
    </div>
  );
}

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <Heading as="h1" className="hero__title">
          {siteConfig.title}
        </Heading>
        <p className={clsx('hero__subtitle', styles.heroSubtitle)}>
          A productivity application, made for managing focused work sessions through Arcs, Chapters, and Tasks.
        </p>
        <div className={styles.buttons}>
          <Link className="button button--secondary button--lg" to="/docs/">
            Get Started →
          </Link>
          <Link className={clsx('button button--lg', styles.buttonOutlineWhite)} to="/docs/api/focusarc-api">
            API Reference
          </Link>
        </div>
      </div>
    </header>
  );
}

export default function Home(): ReactNode {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout title={siteConfig.title} description="FocusArc — productivity API documentation">
      <HomepageHeader />
      <main>
        <section className={styles.features}>
          <div className="container">
            <div className="row">
              {features.map((props) => (
                <Feature key={props.title} {...props} />
              ))}
            </div>
          </div>
        </section>
      </main>
    </Layout>
  );
}