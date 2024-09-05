import { ChevronRightIcon } from "@heroicons/react/24/solid";
import { Form, Formik } from "formik";
import { useNavigate, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import amazonLogoDark from "../../public/assets/images/amazon-dark.png";
import LoginInput from "./LoginInput";
import * as Yup from "yup";
import ButtonInput from "./ButtonInput";
import { useAuthContext } from "../../store/AuthContext";
import toast from "react-hot-toast";
import api, { queryClient } from 'util/api'
import { jwtDecode } from "jwt-decode";
import {useMutation }   from '@tanstack/react-query';


const apiUrl = process.env.REACT_APP_API_URL;

const initialUser = {
    email: "",
    password: "",
    login_error: "",
};

const login = async (user) => {
    const data  = await api.post("/auth/public/signin", user);
    return data;
}

const LOGIN_QUERY_KEY = 'login';


const SignInPage = () => {
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const { setToken, token, setRefeshTokenExpired, setCurrentUser } = useAuthContext();
    const navigate = useNavigate();
    const [needHelp, setNeedHelp] = useState(false);
    const [user, setUser] = useState(initialUser);
    const { email, password, login_error } = user;



    const handleChange = (e) => {
        const { name, value } = e.target;
        setUser({
            ...user,
            [name]: value,
        });
    };

    const { mutate, isLoading, isError } = useMutation({
        mutationFn : login, 
        onSuccess: (response) => {

            console.log(response);
            const access = response.headers['access'];

            const decodedToken = jwtDecode(access);
            if (decodedToken.is2faEnabled) {
                setStep(2); // Move to 2FA verification step
            } else {
                //handleSuccessfulLogin(response.data.jwtToken, decodedToken);              
                handleSuccessfulLogin(response, decodedToken);
            }
            queryClient.invalidateQueries({ querykey: [LOGIN_QUERY_KEY] });
            navigate('/');
        },
        onError: (error) => {
            console.log(error);
        }
    });

    const loginValidation = Yup.object({
        // email: Yup.string()
        //     .required("Email address is required.")
        //     .email("Please endter a valid address"),
        password: Yup.string().required("Please enter a password."),
    });

    const handleSuccessfulLogin = (response, decodedToken) => {
        const accessToken = response.headers['access'];

        const user = {
            email: decodedToken.sub,
            roles: decodedToken.roles ? decodedToken.roles.split(",") : [],
        };
        localStorage.setItem("access_token", accessToken);
        localStorage.setItem("USER", JSON.stringify(user));

        //store the token on the context state  so that it can be shared any where in our application by context provider
        setToken(accessToken);

        setRefeshTokenExpired(false);

        setCurrentUser(user);

    };

    const signInHandler = async () => {

        
        mutate({ email, password });



        // const data = {
        //     email,
        //     password
        // }
        // try {
        //     setLoading(true);


        //     const response = await api.post("/auth/public/signin", data);


        //     if (response.status === 200 && response.data) {

        //         const access = response.headers['access'];


        //         const decodedToken = jwtDecode(access);


        //         if (decodedToken.is2faEnabled) {
        //             setStep(2); // Move to 2FA verification step
        //         } else {
        //             //handleSuccessfulLogin(response.data.jwtToken, decodedToken);              

        //             handleSuccessfulLogin(response, decodedToken);
        //         }


        //     } else {
        //         toast.error(
        //             "Login failed. Please check your credentials and try again."
        //         );
        //     }
        // } catch (error) {
        //     if (error) {
        //         toast.error("Invalid credentials");
        //     }
        // } finally {

        //     setLoading(false);
        // }
    };


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
                    <h3 className="text-xl font-bold">Sign in</h3>
                    <Formik
                        enableReinitialize
                        initialValues={{
                            email,
                            password,
                        }}
                        validationSchema={loginValidation}
                        onSubmit={() => signInHandler()}
                    >
                        {(form) => (
                            <Form >
                                <LoginInput
                                    id="input-email"
                                    type="text"
                                    icon="email"
                                    name="email"
                                    placeholder="please type Email Address"
                                    onChange={handleChange}
                                />

                                <LoginInput
                                    id="input-passowrd"
                                    type="password"
                                    icon="password"
                                    name="password"
                                    placeholder="please type Password"
                                    onChange={handleChange}
                                />
                                <ButtonInput type="submit" text="Sign in" />
                            </Form>
                        )}
                    </Formik>

                    <p className="text-xs mt-2">
                        {
                            "By continuing, you agree to Amazon's Conditions of Use and Privacy Notice."
                        }
                    </p>
                    <div className="flex">
                        {login_error && (
                            <p className="text-red-500">{login_error}</p>
                        )}
                    </div>

                    <div
                        onClick={() => setNeedHelp(!needHelp)}
                        className="text-xs flex flex-col cursor-pointer"
                    >
                        <div className="flex items-center">
                            <ChevronRightIcon
                                className={`h-3 text-gray-500" ${needHelp && "rotate-90"
                                    }`}
                            />
                            <span className="text-blue-500 hover:text-amazon-orange hover:underline ml-1">
                                Need help?
                            </span>
                        </div>
                        {needHelp && (
                            <div className="flex flex-col ml-4 mt-2 space-y-2">
                                <Link
                                    to="/forgot"
                                    className="text-blue-500 hover:text-amazon-orange hover:underline"
                                >
                                    Forgot your password?
                                </Link>
                                <Link
                                    href="/"
                                    className="text-blue-500 hover:text-amazon-orange hover:underline"
                                >
                                    Other issues with Sign-In
                                </Link>
                            </div>
                        )}
                    </div>
                </div>

                <div className="flex flex-col mt-3">
                    <span
                        className="pt-1 relative flex justify-center text-sm 
                before:left-1 before:top-[50%] before:absolute before:bg-slate-200 before:h-[1px] before:w-[20%] sm:before:w-[25%] md:before:w-[31%]
                after:right-1 after:top-[50%] after:absolute after:bg-slate-200 after:h-[1px] after:w-[20%] sm:after:w-[25%] md:after:w-[31%]"
                    >
                        New to Amazon?
                    </span>
                    <Link
                        to="/register"
                        className="flex items-center justify-center w-full mt-4 button-orange  py-[0.5rem] text-sm text-gray-900 active:from-amazon-orange active:to-yellow-200 "
                    >
                        Create your Amazon account
                    </Link>
                </div>
            </div>

        </>

    );
}

export default SignInPage;