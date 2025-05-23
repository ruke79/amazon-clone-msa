import 'components/error/PageContent.module.css';

function PageContent({ title, children }) {
  return (
    <div className="content">
      <h1>{title}</h1>
      {children}
    </div>
  );
}

export default PageContent;
