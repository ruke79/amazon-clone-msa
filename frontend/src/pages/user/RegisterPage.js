import { ChevronRightIcon } from "@heroicons/react/24/solid";
import { Form, Formik } from "formik";
import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import amazonLogoDark from "../../public/assets/images/amazon-dark.png";
import * as Yup from "yup";
import api, { postRequest } from '../../util/api'
import LoginInput from "./LoginInput";
import ButtonInput from "./ButtonInput";
import { useAuthContext } from "../../store/AuthContext";
import { useErrorBoundary } from "react-error-boundary";

const initialUser = {
    username: "",
    email: "",
    password: "",
    conf_password: "",
    success: "",
    error: ""
};

const RegisterPage = () => {
    const [loading, setLoading] = useState(false);
    const [user, setUser] = useState(initialUser);
    const [role, setRole] = useState();
    const { username, email, password, conf_password, success, error } = user;
    const { token } = useAuthContext();
    const navigate = useNavigate();
    const { showBoundary } = useErrorBoundary();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUser({
            ...user,
            [name]: value,
        });
        
    };

    useEffect(() => {
        setRole("ROLE_USER");
    }, []);

    useEffect(() => {
        if (token) navigate("/");
      }, [navigate, token]);

    const registerValidation = Yup.object({
        username: Yup.string().required("What's your name?").min(2,"First name must be between 2 and 16 characters.").max(16,"First name must be between 2 and 16 characters.").matches(/^[aA-zZ]/,"Numbers and Special characters are not allowed"),
        email: Yup.string()
            .required("Email address is required.")
            .email("Please enter a valid address"),
        password: Yup.string().required("Please enter a password.").min(6,"Password must be atleast 6 characters.").max(36,"password can't be more than 36 characters."),
        conf_password: Yup.string().required("Confirm our password.").oneOf([Yup.ref("password")], "Passwords must match.")
    });

    const signUpHandler = async (data) => {
        const { username, email, password } = data;
        const sendData = {
            username,
            email,
            password,
            role: [role],
          };    
        try{
            setLoading(true);
            
            const response = await postRequest("/auth/public/register", sendData);

            setUser({
                ...user, error: "", success: response.message
            })
            if (response.data) {
                navigate("/signin");
              }

        } catch(error) {
            setLoading(false);
            setUser({
                ...user, success: "", error: error.response.data.message
            })
            showBoundary(error);
        }
    }



    return (
        <>
        <div className="flex flex-col mx-auto w-full px-4 sm:w-3/5 md:w-3/5 lg:w-2/5  pt-8 pb-16">
            <div className="mx-auto my-2">
                <Link to="/">
                    <img
                        src={amazonLogoDark}
                        alt="amazon-logo"
                        className="object-contain w-28 md:w-48 pt-2"
                    />
                </Link>
            </div>
            <div className="flex flex-col p-4 my-4 bg-white rounded border space-y-4">
                <h3 className="text-xl font-bold">Sign Up</h3>
                <Formik
                    enableReinitialize
                    initialValues={{
                        username,
                        email,
                        password,
                        conf_password,
                    }}
                    validationSchema={registerValidation}
                    onSubmit={ signUpHandler }
                >
                    {(form) => (
                        <Form>
                            <LoginInput
                                id="input-name"
                                type="text"
                                icon="user"
                                name="username"
                                placeholder="your Name"
                                onChange={handleChange}
                            />
                            <LoginInput
                                id="input-email"
                                type="text"
                                icon="email"
                                name="email"
                                placeholder="Email Address"
                                onChange={handleChange}
                            />

                            <LoginInput
                                id="input-passowrd"
                                type="password"
                                icon="password"
                                name="password"
                                placeholder="Password"
                                onChange={handleChange}
                            />
                            <LoginInput
                                id="input-passowrd-conf"
                                type="password"
                                icon="password"
                                name="conf_password"
                                placeholder="Re-type Password"
                                onChange={handleChange}
                            />
                            <ButtonInput type="submit" text="Sign up" />
                        </Form>
                    )}
                </Formik>

                <div className="flex">
                    {error ? (
                        <p className="text-red-500">{error}</p>
                    ) : success ? (
                        <p className="text-green-500">{success}</p>
                    ) : ''}
                </div>

                <p className="text-xs my-2">
                    {"By continuing, you agree to Amazon's Conditions of Use and Privacy Notice."}
                </p>

                
                <span className="pt-1 relative flex justify-center text-sm 
                before:left-1 before:top-[50%] before:absolute before:bg-slate-200 before:h-[1px] before:w-[10%] sm:before:w-[18%] md:before:w-[22%]
                after:right-1 after:top-[50%] after:absolute after:bg-slate-200 after:h-[1px] after:w-[10%] sm:after:w-[18%] md:after:w-[22%]">
                    sign up with another Accounts
                </span>

                <div className="text-sm flex items-center pt-4">
                    <span className="text-black ml-1">
                        Already have an account?
                    </span>
                    <Link
                        className="flex items-center text-blue-500 hover:text-amazon-orange hover:underline ml-2"
                        to="/signin"
                    >
                        Sign In
                        <ChevronRightIcon className="h-3 text-gray-500"/>
                    </Link>
                </div>
            </div>
        </div> 
        
        </>
    );
}

export default RegisterPage;