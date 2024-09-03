import Layout from "components/profile/Layout";
import { redirect, useLoaderData } from "react-router-dom";
import { useAuthContext } from "store/AuthContext";
import api from "util/api";

const Profile = () => {
    const { tab } = useLoaderData();
    const { currentUser } = useAuthContext();
    
    
    return (
        <>
            <Layout user={currentUser} tab={tab} title={`${currentUser.username}'s Profile`}>
            <div className="text-center">
                    <h2 className="text-4xl font-bold mb-6">My Profile</h2>
            </div>
            </Layout>
        </>
    );
};

export default Profile;

export const loader = (authContext) => {

    return async ({params, request}) => {
    
        // const { currentUser } = authContext;

        // console.log(currentUser);

        // if (!currentUser) {
        //     return redirect('/')
        // }

        const searchParams = new URL(request.url).searchParams;
        const tab = Number(searchParams.get('tab')) || 0;

        return {
               //user : currentUser,
               tab : tab,                          
        }

    };
}




// export async function getServerSideProps(context) {
//     db.connectDb();
//     const { query } = context;
//     const session = await getSession(context);
//     const user = session?.user;
//     const tab = query.tab || 0;

//     if (!session) {
//         return {
//             redirect: {
//                 destination: "/",
//             },
//         };
//     }

//     return {
//         props: {
//             user,
//             tab,
//         },
//     };
// }
