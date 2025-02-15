import { useRouteError, useNavigate, replace } from 'react-router-dom';
import PageContent from 'components/error/PageContent';
import { useEffect } from 'react';
import Maintenance from 'components/error/Maintenance';
import toast from 'react-hot-toast';

function ErrorPage() {
  const error = useRouteError();
  const navigate = useNavigate();  

  let title = 'An error occurred!';
  let message = 'Something went wrong!';

  if (error.status === 500) {
    message = error.data.message;
  }

  if (error.status === 404) {
    title = 'Not found!';
    message = 'Could not find resource or page.';
  }

  useEffect(() => {

    if(error.code === 'ERR_NETWORK')
      {
        console.log(error.code);
        navigate('error_server', {replace:true});
      }

  }, [])  
  
  const handler = () => {

    toast("Worng Error");
  }
  
  return (
    <>      
    {error.code === 'ERR_NETWORK' ? 
      (<Maintenance/>)
      : (<PageContent title={title}>
        <p>{message}</p>
        <p>{error.code}</p>
        <p>{error.status}</p>
        <button onClick={handler}>Try again</button>
      </PageContent>
      )
    }
    </>
  );
}

export default ErrorPage;
