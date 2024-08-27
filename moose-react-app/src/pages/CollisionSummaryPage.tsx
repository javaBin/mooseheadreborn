import {useContext, useEffect, useState} from "react";
import {AppContext} from "../context/AppContext";
import {RegistrationCollisionType, UserLogin} from "../ServerTypes";
import {Alert, Container} from "react-bootstrap";
import readCollisionSummary from "../hooks/readCollisionSummary";
import CollisionSummaryComponent from "./CollisionSummaryComponent";

const CollisionSummaryPage = () => {
    const appContext = useContext(AppContext);
    const userLogin:UserLogin|null = appContext?.userLogin || null;
    const [errormessage,setErrormessage] = useState<string|null>(null);
    const [collisionList,setCollisionList] = useState<RegistrationCollisionType[]>([]);

    useEffect(() => {
        if (!userLogin?.accessToken) {
            setCollisionList([]);
            return;
        }
        readCollisionSummary(userLogin.accessToken)
            .then(collisionSummary => {
                setCollisionList(collisionSummary.registrationCollisionList);
            })
            .catch(errorFromServer => setErrormessage(errorFromServer));

    },[userLogin]);

    return (<Container>
        <h1>Registration collisions</h1>
        {(userLogin && (collisionList.length === 0)) && <p>No collisions found</p>}
        {collisionList.map((registrationCollision,index) => <CollisionSummaryComponent registrationCollision={registrationCollision} key={index}/>)}
        {errormessage && <Alert variant={"danger"}>{errormessage}</Alert> }
        <div style={{ height: '80px' }}></div>
    </Container>);

}

export default CollisionSummaryPage;