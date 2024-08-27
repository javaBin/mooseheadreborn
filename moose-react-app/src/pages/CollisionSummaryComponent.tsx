import {RegistrationCollisionType} from "../ServerTypes";

interface CollisionSummaryComponentProps {
    registrationCollision:RegistrationCollisionType
}
const CollisionSummaryComponent = ({registrationCollision}:CollisionSummaryComponentProps) => {
    return (<div>
        <h2>{registrationCollision.name} ({registrationCollision.email})</h2>
        <p>{registrationCollision.workshopAName} ({registrationCollision.astart}-{registrationCollision.aend}) - {registrationCollision.statusA} (<a href={"/registration/" + registrationCollision.registrationIdA}>Cancel registration</a>)</p>
        <p>{registrationCollision.workshopBName} ({registrationCollision.bstart}-{registrationCollision.bend}) - {registrationCollision.statusB} (<a href={"/registration/" + registrationCollision.registrationIdB}>Cancel registration</a>)</p>
    </div>);
}

export default CollisionSummaryComponent;