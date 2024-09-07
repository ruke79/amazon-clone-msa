import { ErrorBoundary, FallbackProps } from 'react-error-boundary';
import { AxiosError } from 'axios';
import ApiErrorHandler from './ApiErrorHandler';

export const ReactErrorBoundaryComponent = (props) => {
    const { children } = props;
    return <ErrorBoundary FallbackComponent={ErrorFallback}>      
       <ApiErrorHandler/>
        {children}
      </ErrorBoundary>;
  };

  const ErrorFallback = ({ error, resetErrorBoundary }) => {
    // axios error
    if (error instanceof AxiosError)
      return (
        <>
          <pre>
            react-error-boundary axios error {error.message} | {error.code}
          </pre>
          <button type="button" onClick={resetErrorBoundary}>
            reset button
          </button>
        </>
      );
  
    //   normal error
    return (
      <>
        <pre>react-error-boundary {error.message}</pre>
        <button type="button" onClick={resetErrorBoundary}>
          reset button
        </button>
      </>
    );
  };