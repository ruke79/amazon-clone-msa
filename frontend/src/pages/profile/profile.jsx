import Layout from "components/profile/Layout";
import { redirect, useLoaderData, useSearchParams } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import api, { getRequest } from "util/api";
import tokenUtil from "util/tokenUtil";

const Profile = () => {

    const searchParams = useSearchParams();

    
    const { user } = useAuthContext();
    console.log(user);
    //const { tab } = useLoaderData();
    //const tab = Number(searchParams.get('tab')) ;
    

    return (
        <>
            <Layout user={user} tab={0} title={`${user.email}'s Profile`}>
                <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Profile</h2>
                </div>
            </Layout>
        </>
    );
};


export default Profile;

export const loader = async ({ params, request }) => {

    const searchParams = new URL(request.url).searchParams;
    const tab = Number(searchParams.get('tab')) || 0;

    return {
        tab: tab,
    }

    // return async ({params, request}) => {

    //     const searchParams = new URL(request.url).searchParams;
    //         const tab = Number(searchParams.get('tab')) || 0;


    //     try {
    //         const { data } = await getRequest(`/user-service/api/auth/user`);

    //         return {
    //                user : data,
    //                tab : tab,                          
    //         }
    //     }
    //     catch(err) {

    //     }


    // };
}





